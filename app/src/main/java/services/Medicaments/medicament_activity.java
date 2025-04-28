package services.Medicaments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.HomeActivity;
import com.example.applicationproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import services.Appointement.ApptsActivity;
import services.contacts.ContactsActivity;
import services.prescriptions.PrescriptionsActivity;

public class medicament_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicamentAdapter adapter;
    private MedicamentDAO medicamentDAO;
    private EditText searchEditText;
    private MaterialButton addButton;
    private ImageButton darkModeToggle;

    private static final int REQUEST_ADD_MEDICAMENT = 1;
    private static final int REQUEST_EDIT_MEDICAMENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicament);
        requestNotificationPermission();

        // Initialiser la base de données
        medicamentDAO = new MedicamentDAO(this);
        medicamentDAO.open();

        // Trouver les vues
        recyclerView = findViewById(R.id.MedicamentsRecyclerView);
        searchEditText = findViewById(R.id.searchMedicaments);
        addButton = findViewById(R.id.addMedicamentsButton);
        darkModeToggle = findViewById(R.id.darkModeToggle);

        // Configurer le RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Charger les médicaments
        loadMedicaments();

        // Gérer la recherche
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Gérer le clic sur le bouton d'ajout
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(medicament_activity.this, AddMedicamentActivity.class);
            startActivityForResult(intent, REQUEST_ADD_MEDICAMENT);
        });

        // Gérer le mode sombre (fonctionnalité à implémenter)
        darkModeToggle.setOnClickListener(v -> {
            // Implémenter le mode sombre
        });

        // Configurer la navigation en bas
        setupBottomNavigation();
    }

    private void loadMedicaments() {
        List<Medicament> medicaments = medicamentDAO.getAllMedicaments();
        adapter = new MedicamentAdapter(this, medicaments);
        recyclerView.setAdapter(adapter);

        // Configurer les clics sur les éléments
        adapter.setOnItemClickListener(new MedicamentAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Medicament medicament = adapter.getItem(position);
                Intent intent = new Intent(medicament_activity.this, AddMedicamentActivity.class);
                intent.putExtra("medicament_id", medicament.getId());
                startActivityForResult(intent, REQUEST_EDIT_MEDICAMENT);
            }

            @Override
            public void onDeleteClick(int position) {
                Medicament medicament = adapter.getItem(position);
                showDeleteConfirmationDialog(medicament);
            }

            @Override
            public void onItemClick(int position) {
                // Afficher les détails du médicament si nécessaire
                Medicament medicament = adapter.getItem(position);
                // Implémentez votre logique pour afficher les détails
            }
        });
    }

    private void showDeleteConfirmationDialog(Medicament medicament) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer " + medicament.getNom())
                .setMessage("Êtes-vous sûr de vouloir supprimer ce médicament ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    medicamentDAO.deleteMedicament(medicament.getId());
                    loadMedicaments();
                    // Supprimer les notifications associées
                    cancelAllNotifications(medicament);
                })
                .setNegativeButton("Annuler", null)
                .show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == REQUEST_ADD_MEDICAMENT || requestCode == REQUEST_EDIT_MEDICAMENT)) {
            loadMedicaments();
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_meds); // Sélectionner l'item actif

        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_meds) {
                return true; // Déjà sur cette page
            } else if (itemId == R.id.nav_appts) {
                startActivity(new Intent(this, ApptsActivity.class));
                return true;
            } else if (itemId == R.id.nav_presc) {
                startActivity(new Intent(this, PrescriptionsActivity.class));
                return true;
            } else if (itemId == R.id.nav_contacts) {
                startActivity(new Intent(this, ContactsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        medicamentDAO.open();
        loadMedicaments();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}