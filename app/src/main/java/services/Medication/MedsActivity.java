package services.Medication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import services.ApptsActivity;
import services.ContactsActivity;
import services.SchedActivity;
import com.example.applicationproject.HomeActivity;

public class MedsActivity extends AppCompatActivity {

    RecyclerView medsRecyclerView;
    TextView emptyMessage;
    MedicationAdapter adapter;
    MedicationRepository repo;
    FloatingActionButton addMedButton;
    @Override
    protected void onResume() {
        super.onResume();
        reloadMedications(); // Recharge à chaque fois qu'on revient sur l'écran
    }

    private void reloadMedications() {
        List<Medication> meds = repo.getAllMedications(1); // Utilise ici l'ID réel de l'utilisateur

        adapter.setMedications(meds); // Mets à jour la liste dans l'adapter

        if (meds.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            emptyMessage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meds);

        medsRecyclerView = findViewById(R.id.medsRecyclerView);
        emptyMessage = findViewById(R.id.emptyMessage);
        addMedButton = findViewById(R.id.addMedButton);

        medsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        repo = new MedicationRepository(this);
        List<Medication> meds = repo.getAllMedications(1); // Remplacer par l'ID réel de l'utilisateur

        if (meds.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            emptyMessage.setVisibility(View.GONE);
        }

        adapter = new MedicationAdapter(this, meds, repo);
        medsRecyclerView.setAdapter(adapter);

        // Bouton ajouter
        addMedButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AddMedicationActivity.class));
        });

        // Barre de navigation
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_meds);
        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_appts) {
                startActivity(new Intent(this, ApptsActivity.class));
                return true;
            } else if (itemId == R.id.nav_sched) {
                startActivity(new Intent(this, SchedActivity.class));
                return true;
            } else if (itemId == R.id.nav_contacts) {
                startActivity(new Intent(this, ContactsActivity.class));
                return true;
            }
            return false;
        });

    }
}
