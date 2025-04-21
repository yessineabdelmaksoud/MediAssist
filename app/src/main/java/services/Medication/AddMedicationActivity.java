package services.Medication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.applicationproject.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMedicationActivity extends AppCompatActivity {

    EditText editMedName, editPosologie, editFrequence, editHeure, editRemarque;
    Button btnAddMedication, btnAddPhoto;
    ImageView imgMedicament;

    boolean isEditMode = false;
    int medId = -1;
    private MedicationRepository repo;
    private String currentPhotoPath = "";

    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_GALLERY_PERMISSION = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);
        requestNotificationPermission();

        editMedName = findViewById(R.id.editMedName);
        editPosologie = findViewById(R.id.editPosologie);
        editFrequence = findViewById(R.id.editFrequence);
        editHeure = findViewById(R.id.editHeure);
        editRemarque = findViewById(R.id.editRemarque);
        btnAddMedication = findViewById(R.id.btnAddMedication);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        imgMedicament = findViewById(R.id.imgMedicament);

        repo = new MedicationRepository(this);

        initializeLaunchers();

        editHeure.setOnClickListener(v -> showTimePicker());
        btnAddPhoto.setOnClickListener(v -> showImageSourceDialog());


        // Check if we are in edit mode
        Intent intent = getIntent();
        if (intent.hasExtra("edit") && intent.getBooleanExtra("edit", false)) {
            isEditMode = true;
            medId = intent.getIntExtra("id", -1);

            editMedName.setText(intent.getStringExtra("nom"));
            editPosologie.setText(intent.getStringExtra("posologie"));
            editFrequence.setText(intent.getStringExtra("frequence"));
            editHeure.setText(intent.getStringExtra("heure"));
            editRemarque.setText(intent.getStringExtra("remarque"));

            // Charger l'image si elle existe
            currentPhotoPath = intent.getStringExtra("photo_path");
            if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                loadImageFromPath(currentPhotoPath);
            }

            btnAddMedication.setText("Modifier Médicament");
        }

        btnAddMedication.setOnClickListener(v -> {
            String nom = editMedName.getText().toString().trim();
            String posologie = editPosologie.getText().toString().trim();
            String frequence = editFrequence.getText().toString().trim();
            String heure = editHeure.getText().toString().trim();
            String remarque = editRemarque.getText().toString().trim();


            if (nom.isEmpty() || heure.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir au moins le nom et l’heure.", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = 1; // Replace with the actual ID of the logged-in user
            Medication med = new Medication(userId, nom, posologie, frequence, heure, remarque, currentPhotoPath);

            if (isEditMode) {
                med.setId(medId);
                int result = repo.updateMedication(med);
                if (result > 0) {
                    // Schedule the alarm for the updated medication
                    scheduleAlarm(heure, nom);
                    Toast.makeText(this, "Médicament modifié avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Échec de la modification", Toast.LENGTH_SHORT).show();
                }
            } else {
                long result = repo.addMedication(med);
                if (result != -1) {
                    // Schedule the alarm for the new medication
                    scheduleAlarm(heure, nom);
                    Toast.makeText(this, "Médicament ajouté !", Toast.LENGTH_SHORT).show();
                    finish(); // go back to the list
                } else {
                    Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeLaunchers() {
        // Launcher pour prendre une photo
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadImageFromPath(currentPhotoPath);
                    }
                });

        // Launcher pour sélectionner depuis la galerie
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        if (selectedImage != null) {
                            try {
                                // Copier l'image dans le dossier de l'application
                                currentPhotoPath = saveImageToAppFolder(selectedImage);
                                loadImageFromPath(currentPhotoPath);
                            } catch (IOException e) {
                                Toast.makeText(this, "Erreur lors de la sauvegarde de l'image", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisir une source");
        builder.setItems(new CharSequence[]{"Prendre une photo", "Galerie"}, (dialog, which) -> {
            if (which == 0) {  // Camera
                if (checkCameraPermission()) {
                    dispatchTakePictureIntent();
                }
            } else {  // Gallery
                if (checkGalleryPermission()) {
                    openGallery();
                }
            }
        });
        builder.show();
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return false;
        }
        return true;
    }

    private boolean checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_GALLERY_PERMISSION);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_PERMISSION);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permission de caméra refusée", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission de galerie refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Erreur lors de la création du fichier image", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.applicationproject.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureLauncher.launch(takePictureIntent);
            }
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String saveImageToAppFolder(Uri sourceUri) throws IOException {
        File destinationFile = createImageFile();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), sourceUri);
            // Vous pourriez compresser/redimensionner l'image ici si nécessaire
            // bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(destinationFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return destinationFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void loadImageFromPath(String path) {
        if (path != null && !path.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                imgMedicament.setImageBitmap(bitmap);
            } else {
                imgMedicament.setImageResource(R.drawable.meds); // Image par défaut si erreur
            }
        }
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String time = String.format("%02d:%02d", hourOfDay, minute1);
            editHeure.setText(time);
        }, hour, minute, true).show();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleAlarm(String heure, String medName) {
        String[] parts = heure.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        // If the time is in the past, set the alarm for the next day
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("med_name", medName);
        // Generate a unique request code for each alarm
        int requestCode = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }


    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }
}
