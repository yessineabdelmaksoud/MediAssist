package account;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.applicationproject.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {

    private ImageView backButton;
    private CircleImageView profileImageView;
    private ImageView changeProfileImage;
    private EditText usernameField, ageField, weightField, heightField, phoneField, addressField, medicalConditionsField;
    private Spinner genderSpinner, bloodTypeSpinner;
    private Button saveButton;

    private ProfileDAO profileDAO;
    private UserProfile userProfile;
    private String username;
    private static final String PREFS_NAME = "LoginPrefs";

    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Initialize UI components
        backButton = findViewById(R.id.backButton11);
        profileImageView = findViewById(R.id.p);
        changeProfileImage = findViewById(R.id.changeProfileImage);
        usernameField = findViewById(R.id.u);
        ageField = findViewById(R.id.ageField);
        genderSpinner = findViewById(R.id.genderSpinner);
        bloodTypeSpinner = findViewById(R.id.bloodTypeSpinner);
        weightField = findViewById(R.id.weightField);
        heightField = findViewById(R.id.heightField);
        phoneField = findViewById(R.id.phoneField);
        addressField = findViewById(R.id.addressField);
        medicalConditionsField = findViewById(R.id.medicalConditionsField);
        saveButton = findViewById(R.id.saved);

        // Get username from shared preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        username = settings.getString("username", "");

        // Set up gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Set up blood type spinner
        ArrayAdapter<CharSequence> bloodTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.blood_type_array, android.R.layout.simple_spinner_item);
        bloodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodTypeSpinner.setAdapter(bloodTypeAdapter);

        // Initialize database access
        profileDAO = new ProfileDAO(this);
        profileDAO.open();

        // Register for camera activity result
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        File imgFile = new File(currentPhotoPath);
                        if (imgFile.exists()) {
                            profileImageView.setImageURI(Uri.fromFile(imgFile));
                        }
                    }
                });

        // Register for gallery picker result
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        if (selectedImage != null) {
                            try {
                                // Créer un fichier pour sauvegarder une copie de l'image sélectionnée
                                File imageFile = createImageFile();
                                saveImageFromUri(selectedImage, imageFile);

                                // Mettre à jour le chemin de l'image et afficher l'image
                                currentPhotoPath = imageFile.getAbsolutePath();
                                profileImageView.setImageURI(Uri.fromFile(imageFile));
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(AccountSettingsActivity.this,
                                        "Erreur lors de la copie de l'image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        // Load user profile
        loadUserProfile();

        // Set button click listeners
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileDAO.close();
    }

    private void loadUserProfile() {
        // Get user profile from database
        userProfile = profileDAO.getProfileByUsername(username);

        if (userProfile == null) {
            userProfile = new UserProfile(username);
            profileDAO.saveProfile(userProfile);
        }

        // Populate form fields with user data
        usernameField.setText(username);

        if (userProfile.getAge() > 0) {
            ageField.setText(String.valueOf(userProfile.getAge()));
        }

        // Set gender spinner selection
        if (userProfile.getGender() != null && !userProfile.getGender().isEmpty()) {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) genderSpinner.getAdapter();
            int position = adapter.getPosition(userProfile.getGender());
            if (position >= 0) {
                genderSpinner.setSelection(position);
            }
        }

        // Set blood type spinner selection
        if (userProfile.getBloodType() != null && !userProfile.getBloodType().isEmpty()) {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) bloodTypeSpinner.getAdapter();
            int position = adapter.getPosition(userProfile.getBloodType());
            if (position >= 0) {
                bloodTypeSpinner.setSelection(position);
            }
        }

        if (userProfile.getWeight() > 0) {
            weightField.setText(String.format(Locale.getDefault(), "%.1f", userProfile.getWeight()));
        }

        if (userProfile.getHeight() > 0) {
            heightField.setText(String.format(Locale.getDefault(), "%.1f", userProfile.getHeight()));
        }

        if (userProfile.getPhone() != null) {
            phoneField.setText(userProfile.getPhone());
        }

        if (userProfile.getAddress() != null) {
            addressField.setText(userProfile.getAddress());
        }

        if (userProfile.getMedicalConditions() != null) {
            medicalConditionsField.setText(userProfile.getMedicalConditions());
        }

        // Load profile image if it exists
        if (userProfile.getProfileImagePath() != null && !userProfile.getProfileImagePath().isEmpty()) {
            File imgFile = new File(userProfile.getProfileImagePath());
            if (imgFile.exists()) {
                profileImageView.setImageURI(Uri.fromFile(imgFile));
                currentPhotoPath = userProfile.getProfileImagePath();
            }
        }
    }

    private void saveUserProfile() {
        // Save form data to UserProfile object
        if (!ageField.getText().toString().isEmpty()) {
            try {
                userProfile.setAge(Integer.parseInt(ageField.getText().toString()));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Veuillez entrer un âge valide", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        userProfile.setGender(genderSpinner.getSelectedItem().toString());
        userProfile.setBloodType(bloodTypeSpinner.getSelectedItem().toString());

        if (!weightField.getText().toString().isEmpty()) {
            try {
                userProfile.setWeight(Float.parseFloat(weightField.getText().toString()));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Veuillez entrer un poids valide", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!heightField.getText().toString().isEmpty()) {
            try {
                userProfile.setHeight(Float.parseFloat(heightField.getText().toString()));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Veuillez entrer une taille valide", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        userProfile.setPhone(phoneField.getText().toString());
        userProfile.setAddress(addressField.getText().toString());
        userProfile.setMedicalConditions(medicalConditionsField.getText().toString());

        if (currentPhotoPath != null) {
            userProfile.setProfileImagePath(currentPhotoPath);
        }

        // Save to database
        long result = profileDAO.saveProfile(userProfile);

        if (result > 0) {
            Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImagePickerOptions() {
        // Create and show a dialog to choose between camera and gallery
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Choisir une source");

        String[] options = {"Caméra", "Galerie"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                dispatchTakePictureIntent();
            } else {
                dispatchPickImageIntent();
            }
        });

        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Toast.makeText(this, "Erreur lors de la création du fichier image", Toast.LENGTH_SHORT).show();
        }

        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.applicationproject.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    private void dispatchPickImageIntent() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(pickPhotoIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Nouvelle méthode pour copier le contenu de l'URI vers un fichier local
    private void saveImageFromUri(Uri sourceUri, File destinationFile) throws IOException {
        ContentResolver contentResolver = getContentResolver();

        try (InputStream in = contentResolver.openInputStream(sourceUri);
             OutputStream out = new FileOutputStream(destinationFile)) {

            if (in == null) {
                throw new IOException("Impossible d'ouvrir l'image sélectionnée");
            }

            // Copie les données d'entrée vers le fichier de sortie
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();
        }
    }
}