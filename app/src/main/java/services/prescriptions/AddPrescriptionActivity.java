package services.prescriptions;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.applicationproject.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddPrescriptionActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    private EditText titleEditText, doctorEditText, noteEditText;
    private TextView dateTextView;
    private ImageView prescriptionImagePreview;
    private LinearLayout datePickerLayout, imagePickerLayout;
    private Button saveButton;
    private ImageButton backButton;

    private PrescriptionDatabaseHelper dbHelper;
    private Calendar selectedDate;
    private String currentPhotoPath;
    private long prescriptionId = -1;
    private Prescription currentPrescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prescription);

        // Initialisation de la base de données
        dbHelper = new PrescriptionDatabaseHelper(this);

        // Récupérer les références des vues
        titleEditText = findViewById(R.id.titleEditText);
        doctorEditText = findViewById(R.id.doctorEditText);
        dateTextView = findViewById(R.id.dateTextView);
        noteEditText = findViewById(R.id.noteEditText);
        prescriptionImagePreview = findViewById(R.id.prescriptionImagePreview);
        datePickerLayout = findViewById(R.id.datePickerLayout);
        imagePickerLayout = findViewById(R.id.imagePickerLayout);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton1);

        // Initialiser la date à aujourd'hui
        selectedDate = Calendar.getInstance();
        updateDateDisplay();

        // Vérifier s'il s'agit d'une modification
        prescriptionId = getIntent().getLongExtra("prescription_id", -1);
        if (prescriptionId != -1) {
            loadPrescription(prescriptionId);
        }

        // Gestion des clics
        backButton.setOnClickListener(v -> finish());

        datePickerLayout.setOnClickListener(v -> showDatePicker());

        imagePickerLayout.setOnClickListener(v -> showImageSourceDialog());

        saveButton.setOnClickListener(v -> savePrescription());
    }

    private void loadPrescription(long id) {
        currentPrescription = dbHelper.getPrescription(id);
        if (currentPrescription != null) {
            titleEditText.setText(currentPrescription.getTitle());
            doctorEditText.setText(currentPrescription.getDoctorName());
            noteEditText.setText(currentPrescription.getNote());

            // Définir la date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(currentPrescription.getDate());
                if (date != null) {
                    selectedDate.setTime(date);
                    updateDateDisplay();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Charger l'image si elle existe
            if (currentPrescription.getImagePath() != null && !currentPrescription.getImagePath().isEmpty()) {
                currentPhotoPath = currentPrescription.getImagePath();
                File imgFile = new File(currentPhotoPath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    prescriptionImagePreview.setImageBitmap(myBitmap);
                    prescriptionImagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateTextView.setText(sdf.format(selectedDate.getTime()));
    }

    private void showImageSourceDialog() {
        String[] options = {"Prendre une photo", "Choisir depuis la galerie"};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Ajouter une image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        dispatchTakePictureIntent();
                    } else {
                        pickImageFromGallery();
                    }
                })
                .show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = Utils.createImageFile(this);
                currentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                Toast.makeText(this, "Erreur lors de la création du fichier", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.applicationproject.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                setImageToPreview(currentPhotoPath);
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                Uri selectedImage = data.getData();
                try {
                    File photoFile = Utils.createImageFileFromUri(this, selectedImage);
                    currentPhotoPath = photoFile.getAbsolutePath();
                    setImageToPreview(currentPhotoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erreur lors de la récupération de l'image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setImageToPreview(String path) {
        if (path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                prescriptionImagePreview.setImageBitmap(bitmap);
                prescriptionImagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }

    private void savePrescription() {
        String title = titleEditText.getText().toString().trim();
        String doctor = doctorEditText.getText().toString().trim();
        String note = noteEditText.getText().toString().trim();
        String date = dateTextView.getText().toString().trim();

        if (title.isEmpty()) {
            titleEditText.setError("Veuillez entrer un titre");
            return;
        }

        Prescription prescription = new Prescription(title, doctor, date, note, currentPhotoPath);

        if (prescriptionId != -1) {
            // Mode édition
            prescription.setId(prescriptionId);
            dbHelper.updatePrescription(prescription);
            Toast.makeText(this, "Prescription mise à jour", Toast.LENGTH_SHORT).show();
        } else {
            // Mode ajout
            long id = dbHelper.addPrescription(prescription);
            if (id != -1) {
                Toast.makeText(this, "Prescription ajoutée", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}