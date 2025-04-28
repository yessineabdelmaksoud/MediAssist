package services.Appointement;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applicationproject.R;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import services.Medicaments.medicament_activity;
import services.contacts.ContactsActivity;
import services.prescriptions.PrescriptionsActivity;

public class ApptsActivity extends AppCompatActivity implements AppointmentAdapter.OnAppointmentActionListener {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private AppointmentDbHelper dbHelper;
    private EditText searchEditText;
    private MaterialButton addButton;
    private ImageButton darkModeToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        // Initialize views
        recyclerView = findViewById(R.id.AppointmentsRecyclerView);
        searchEditText = findViewById(R.id.searchAppointments);
        addButton = findViewById(R.id.addAppointments);
        darkModeToggle = findViewById(R.id.darkModeToggle);

        // Initialize database helper
        dbHelper = new AppointmentDbHelper(this);

        // Setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadAppointments();

        // Setup search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    List<Appointment> searchResults = dbHelper.searchAppointments(s.toString());
                    adapter.updateAppointmentList(searchResults);
                } else {
                    loadAppointments();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Setup add button
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ApptsActivity.this, AddAppointmentActivity.class);
            startActivity(intent);
        });

        // Setup dark mode toggle (functionality to be implemented)
        darkModeToggle.setOnClickListener(v -> {
            // Implement dark mode toggle
        });

        // Setup bottom navigation
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_appts);
        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_meds) {
                startActivity(new Intent(this, medicament_activity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_appts) {
                // Already on Appointments
                return true;
            } else if (itemId == R.id.nav_presc) {
                startActivity(new Intent(this, PrescriptionsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_contacts) {
                startActivity(new Intent(this, ContactsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
    }

    private void loadAppointments() {
        List<Appointment> appointments = dbHelper.getAllAppointments();
        adapter = new AppointmentAdapter(this, appointments, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAppointmentDeleted() {
        // Reload appointments after deletion
        loadAppointments();
    }

    @Override
    public void onAppointmentEdit(Appointment appointment) {
        Intent intent = new Intent(ApptsActivity.this, AddAppointmentActivity.class);
        intent.putExtra("appointment_id", appointment.getId());
        startActivity(intent);
    }
}