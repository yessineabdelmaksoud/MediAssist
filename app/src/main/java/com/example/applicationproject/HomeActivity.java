package com.example.applicationproject;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import services.ApptsActivity;
import services.ContactsActivity;
import services.Medication.MedsActivity;
import services.SchedActivity;

public class HomeActivity extends AppCompatActivity {

    TextView helloText, dateText;
    String username = "User"; // À récupérer depuis Intent ou SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        helloText = findViewById(R.id.helloText);
        dateText = findViewById(R.id.dateText);

        String username = getIntent().getStringExtra("username");
        if (username == null) {
            username = "User"; // fallback si null
        }

        helloText.setText("Hello " + username);

        String currentDate = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
        dateText.setText(currentDate);

        // Navigation
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Reste sur Home
                return true;
            } else if (itemId == R.id.nav_meds) {
                startActivity(new Intent(this, MedsActivity.class));
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