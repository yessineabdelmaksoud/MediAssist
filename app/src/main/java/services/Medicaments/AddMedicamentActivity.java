package services.Medicaments;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.applicationproject.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.Manifest;

public class AddMedicamentActivity extends AppCompatActivity {

    private EditText titleEditText, dosageEditText, frequenceEditText, remarqueEditText;
    private CheckBox checkLundi, checkMardi, checkMercredi, checkJeudi, checkVendredi, checkSamedi, checkDimanche;
    private LinearLayout alarmsContainer;
    private Button saveButton, addPhotoButton;
    private ImageButton backButton;
    private ImageView imageView;

    private List<String> heuresList = new ArrayList<>();
    private String currentPhotoPath;
    private boolean isEditMode = false;
    private long medicamentId = -1;
    private MedicamentDAO medicamentDAO;
    private Medicament currentMedicament;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;
    private static final int PERMISSION_REQUEST_CAMERA = 101;
    private static final int PERMISSION_REQUEST_STORAGE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmedication);

        // Initialiser la base de données
        medicamentDAO = new MedicamentDAO(this);
        medicamentDAO.open();

        // Trouver les vues
        initializeViews();

        // Vérifier si nous sommes en mode édition
        medicamentId = getIntent().getLongExtra("medicament_id", -1);
        isEditMode = medicamentId != -1;

        if (isEditMode) {
            currentMedicament = medicamentDAO.getMedicamentById(medicamentId);
            populateFields();
        }

        // Configurer les écouteurs
        backButton.setOnClickListener(v -> onBackPressed());

        addPhotoButton.setOnClickListener(v -> showImageSourceDialog());

        // Gérer l'ajout d'une alarme
        Button addAlarmButton = new Button(this);
        addAlarmButton.setText("+ Ajouter une alarme");
        alarmsContainer.addView(addAlarmButton);

        addAlarmButton.setOnClickListener(v -> showTimePickerDialog());

        saveButton.setOnClickListener(v -> saveButtonClicked());
    }

    private void initializeViews() {
        titleEditText = findViewById(R.id.titleEditTextMedicament);
        dosageEditText = findViewById(R.id.dosageEditTextMedicament);
        frequenceEditText = findViewById(R.id.frequenceEditTextMedicament);
        remarqueEditText = findViewById(R.id.remarqueEditTextmedicament);

        checkLundi = findViewById(R.id.checkbox_lundi);
        checkMardi = findViewById(R.id.checkbox_mardi);
        checkMercredi = findViewById(R.id.checkbox_mercredi);
        checkJeudi = findViewById(R.id.checkbox_jeudi);
        checkVendredi = findViewById(R.id.checkbox_vendredi);
        checkSamedi = findViewById(R.id.checkbox_samedi);
        checkDimanche = findViewById(R.id.checkbox_dimanche);

        alarmsContainer = findViewById(R.id.alarmsContainer);
        saveButton = findViewById(R.id.saveButtonMedicament);
        addPhotoButton = findViewById(R.id.btnAddPhotomedicament);
        backButton = findViewById(R.id.backButtonappointment);
        imageView = findViewById(R.id.imgageMedicament);
    }

    private void populateFields() {
        if (currentMedicament != null) {
            titleEditText.setText(currentMedicament.getNom());
            dosageEditText.setText(currentMedicament.getPosologie());
            frequenceEditText.setText(currentMedicament.getFrequence());
            if (currentMedicament.getRemarque() != null) {
                remarqueEditText.setText(currentMedicament.getRemarque());
            }

            // Charger l'image si disponible
            if (currentMedicament.getImagePath() != null && !currentMedicament.getImagePath().isEmpty()) {
                currentPhotoPath = currentMedicament.getImagePath();
                imageView.setImageURI(Uri.parse(currentPhotoPath));
            }

            // Cocher les jours sélectionnés
            for (String jour : currentMedicament.getJours()) {
                switch (jour) {
                    case "Lundi":
                        checkLundi.setChecked(true);
                        break;
                    case "Mardi":
                        checkMardi.setChecked(true);
                        break;
                    case "Mercredi":
                        checkMercredi.setChecked(true);
                        break;
                    case "Jeudi":
                        checkJeudi.setChecked(true);
                        break;
                    case "Vendredi":
                        checkVendredi.setChecked(true);
                        break;
                    case "Samedi":
                        checkSamedi.setChecked(true);
                        break;
                    case "Dimanche":
                        checkDimanche.setChecked(true);
                        break;
                }
            }

            // Ajouter les heures existantes
            heuresList.addAll(currentMedicament.getHeures());
            refreshAlarmViews();
        }
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteSelected) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteSelected);
                    heuresList.add(time);
                    refreshAlarmViews();
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void refreshAlarmViews() {
        // Supprimer toutes les vues sauf le bouton d'ajout
        if (alarmsContainer.getChildCount() > 1) {
            alarmsContainer.removeViews(0, alarmsContainer.getChildCount() - 1);
        }

        // Ajouter les nouvelles vues d'alarme
        for (int i = 0; i < heuresList.size(); i++) {
            final int index = i;
            View alarmView = LayoutInflater.from(this).inflate(R.layout.item_alarm_time, null);
            TextView timeTextView = alarmView.findViewById(R.id.timeTextView);
            ImageButton deleteButton = alarmView.findViewById(R.id.deleteAlarmButton);

            timeTextView.setText(heuresList.get(i));
            deleteButton.setOnClickListener(v -> {
                heuresList.remove(index);
                refreshAlarmViews();
            });

            alarmsContainer.addView(alarmView, 0);
        }
    }

    private void showImageSourceDialog() {
        String[] options = {"Prendre une photo", "Choisir depuis la galerie"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter une photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissions();
                    } else {
                        checkStoragePermissions();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void checkStoragePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_STORAGE);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_STORAGE);
            } else {
                openGallery();
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permission de caméra refusée", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission de stockage refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageView.setImageURI(Uri.parse(currentPhotoPath));
            } else if (requestCode == REQUEST_GALLERY_IMAGE && data != null) {
                Uri selectedImage = data.getData();
                currentPhotoPath = getRealPathFromURI(selectedImage);
                imageView.setImageURI(selectedImage);
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        android.database.Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return contentUri.getPath();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void saveButtonClicked() {
        String nom = titleEditText.getText().toString().trim();
        String posologie = dosageEditText.getText().toString().trim();
        String frequence = frequenceEditText.getText().toString().trim();
        String remarque = remarqueEditText.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(nom)) {
            titleEditText.setError("Veuillez entrer un nom");
            return;
        }

        if (TextUtils.isEmpty(posologie)) {
            dosageEditText.setError("Veuillez entrer une posologie");
            return;
        }

        if (TextUtils.isEmpty(frequence)) {
            frequenceEditText.setError("Veuillez entrer une fréquence");
            return;
        }

        List<String> jours = new ArrayList<>();
        if (checkLundi.isChecked()) jours.add("Lundi");
        if (checkMardi.isChecked()) jours.add("Mardi");
        if (checkMercredi.isChecked()) jours.add("Mercredi");
        if (checkJeudi.isChecked()) jours.add("Jeudi");
        if (checkVendredi.isChecked()) jours.add("Vendredi");
        if (checkSamedi.isChecked()) jours.add("Samedi");
        if (checkDimanche.isChecked()) jours.add("Dimanche");

        if (jours.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner au moins un jour", Toast.LENGTH_SHORT).show();
            return;
        }

        if (heuresList.isEmpty()) {
            Toast.makeText(this, "Veuillez ajouter au moins une heure d'alarme", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer ou mettre à jour le médicament
        Medicament medicament;

        if (isEditMode) {
            medicament = currentMedicament;
            medicament.setNom(nom);
            medicament.setPosologie(posologie);
            medicament.setFrequence(frequence);
            medicament.setRemarque(remarque);
            medicament.setImagePath(currentPhotoPath);

            // Mise à jour des jours et heures
            medicament.setJours(jours);
            medicament.setHeures(heuresList);

            // Annuler les anciennes notifications
            cancelAllNotifications(medicament);

            // Mettre à jour dans la base de données
            medicamentDAO.updateMedicament(medicament);
        } else {
            medicament = new Medicament();
            medicament.setNom(nom);
            medicament.setPosologie(posologie);
            medicament.setFrequence(frequence);
            medicament.setRemarque(remarque);
            medicament.setImagePath(currentPhotoPath);
            medicament.setJours(jours);
            medicament.setHeures(heuresList);

            // Insérer dans la base de données
            long id = medicamentDAO.createMedicament(medicament);
            medicament.setId(id);
        }

        // Programmer les notifications
        scheduleNotifications(medicament);

        setResult(RESULT_OK);
        finish();
    }

    private void scheduleNotifications(Medicament medicament) {
        NotificationHelper notificationHelper = new NotificationHelper(this);

        for (String jour : medicament.getJours()) {
            for (String heure : medicament.getHeures()) {
                int notificationId = generateNotificationId(medicament.getId(), jour, heure);
                String message = "Il est temps de prendre " + medicament.getNom();

                try {
                    String[] parts = heure.split(":");
                    int hourOfDay = Integer.parseInt(parts[0]);
                    int minute = Integer.parseInt(parts[1]);

                    int dayOfWeek = convertDayStringToCalendarDay(jour);

                    notificationHelper.scheduleNotification(notificationId, dayOfWeek, hourOfDay, minute,
                            medicament.getNom(), message, medicament.getId());
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Erreur de format d'heure", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void cancelAllNotifications(Medicament medicament) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        for (String jour : medicament.getJours()) {
            for (String heure : medicament.getHeures()) {
                int notificationId = generateNotificationId(medicament.getId(), jour, heure);
                notificationHelper.cancelAlarm(notificationId);
            }
        }
    }

    private int generateNotificationId(long medicamentId, String jour, String heure) {
        String idString = medicamentId + jour + heure.replace(":", "");
        return Math.abs(idString.hashCode());
    }

    private int convertDayStringToCalendarDay(String day) {
        switch (day) {
            case "Lundi": return Calendar.MONDAY;
            case "Mardi": return Calendar.TUESDAY;
            case "Mercredi": return Calendar.WEDNESDAY;
            case "Jeudi": return Calendar.THURSDAY;
            case "Vendredi": return Calendar.FRIDAY;
            case "Samedi": return Calendar.SATURDAY;
            case "Dimanche": return Calendar.SUNDAY;
            default: return Calendar.MONDAY; // Par défaut
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        medicamentDAO.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        medicamentDAO.close();
    }
}