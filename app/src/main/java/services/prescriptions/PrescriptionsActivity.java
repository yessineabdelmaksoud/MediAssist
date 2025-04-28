package services.prescriptions;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.HomeActivity;
import com.example.applicationproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import services.Appointement.ApptsActivity;
import services.Medicaments.medicament_activity;
import services.contacts.ContactsActivity;

public class PrescriptionsActivity extends AppCompatActivity implements PrescriptionAdapter.OnPrescriptionClickListener {

    private RecyclerView prescriptionsRecyclerView;
    private PrescriptionAdapter adapter;
    private PrescriptionDatabaseHelper dbHelper;
    private EditText searchEditText;
    private List<Prescription> prescriptionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        // Initialisation de la base de données
        dbHelper = new PrescriptionDatabaseHelper(this);

        // Récupération des vues
        prescriptionsRecyclerView = findViewById(R.id.prescriptionsRecyclerView1);
        searchEditText = findViewById(R.id.searchEditText1);
        MaterialButton addButton = findViewById(R.id.addButton1);

        // Configuration du RecyclerView
        prescriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Chargement des prescriptions
        loadPrescriptions();

        // Gestion du bouton d'ajout
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(PrescriptionsActivity.this, AddPrescriptionActivity.class);
            startActivity(intent);
        });

        // Gestion de la recherche
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    loadPrescriptions();
                } else {
                    searchPrescriptions(s.toString());
                }
            }
        });
        setupNavigation();

    }

    private void setupNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_presc);
        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_meds) {
                startActivity(new Intent(this, medicament_activity.class));
                return true;
            } else if (itemId == R.id.nav_appts) {
                startActivity(new Intent(this, ApptsActivity.class));
                return true;
            } else if (itemId == R.id.nav_presc) {
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
        loadPrescriptions();
    }

    private void loadPrescriptions() {
        prescriptionList = dbHelper.getAllPrescriptions();
        adapter = new PrescriptionAdapter(this, prescriptionList, this);
        prescriptionsRecyclerView.setAdapter(adapter);
    }

    private void searchPrescriptions(String keyword) {
        prescriptionList = dbHelper.searchPrescriptions(keyword);
        adapter.updateData(prescriptionList);
    }

    @Override
    public void onImageClick(Prescription prescription) {
        if (prescription.getImagePath() != null && !prescription.getImagePath().isEmpty()) {
            Intent intent = new Intent(this, FullScreenImageActivity.class);
            intent.putExtra("imagePath", prescription.getImagePath());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Aucune image disponible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditClick(Prescription prescription) {
        Intent intent = new Intent(this, AddPrescriptionActivity.class);
        intent.putExtra("prescription_id", prescription.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Prescription prescription) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la prescription")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette prescription?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    dbHelper.deletePrescription(prescription.getId());
                    loadPrescriptions();
                    Toast.makeText(this, "Prescription supprimée", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Non", null)
                .show();
    }

    @Override
    public void onSaveClick(Prescription prescription) {
        Utils.saveImageToGallery(this, prescription.getImagePath());
    }
}