package com.example.applicationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import account.ProfileActivity;
import database.AppDatabaseHelper;
import login.LoginActivity;
import services.Appointement.ApptsActivity;
import services.Medicaments.medicament_activity;
import services.contacts.ContactsActivity;
import services.prescriptions.PrescriptionsActivity;
import com.example.applicationproject.chatbot.ChatbotActivity; // Import pour le chatbot

public class HomeActivity extends AppCompatActivity {

    private TextView helloText, dateText;
    private String username = "User";
    private int userId = -1;
    private ImageView accountIcon;
    private FloatingActionButton chatFab; // Ajout du bouton flottant
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        accountIcon = findViewById(R.id.accountIcon);
        chatFab = findViewById(R.id.chatFab); // Récupération du bouton flottant du layout
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        helloText = findViewById(R.id.helloText);
        dateText = findViewById(R.id.dateText);

        // Get the username from SharedPreferences
        username = sharedPreferences.getString("username", "User");
        userId = sharedPreferences.getInt("userId", -1);

        // Récupérer le nom d'utilisateur depuis l'Intent
        String intentUsername = getIntent().getStringExtra("username");
        if (intentUsername != null && !intentUsername.isEmpty()) {
            username = intentUsername;
            // Save username to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.apply();
        }

        // Récupérer l'ID utilisateur depuis l'Intent
        if (getIntent().hasExtra("userId")) {
            userId = getIntent().getIntExtra("userId", -1);
            // Save userId to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("userId", userId);
            editor.apply();
        }

        helloText.setText("Hello " + username);

        // Mettre à jour la date actuelle
        String currentDate = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
        dateText.setText(currentDate);

        // Gestion du clic sur l'icône de compte
        accountIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // Configuration du bouton flottant pour le chatbot
        chatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatbot();
            }
        });

        // Navigation
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Reste sur Home
                return true;
            } else if (itemId == R.id.nav_meds) {
                startActivity(new Intent(this, medicament_activity.class));
                return true;
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

    // Méthode pour ouvrir l'activité Chatbot
    private void openChatbot() {
        Intent intent = new Intent(HomeActivity.this, ChatbotActivity.class);
        startActivity(intent);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.account_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_logout) {
                    logout();
                    return true;
                } else if (item.getItemId() == R.id.menu_account) {
                    openAccountSettings();
                    return true;
                } else if (item.getItemId() == R.id.menu_settings) {
                    openSettings();
                    return true;
                } else if (item.getItemId() == R.id.menu_aboutus) {
                    openaboutus();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openAccountSettings() {
        // Passer userId à ProfileActivity
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("userId", userId);  // Ajout de l'ID utilisateur
        startActivity(intent);
    }

    private void openSettings() {
        // Ouvrir les paramètres généraux
        Toast.makeText(HomeActivity.this, "Open Settings", Toast.LENGTH_SHORT).show();
    }

    private void openaboutus() {
        // Ouvrir les paramètres généraux
        Toast.makeText(HomeActivity.this, "Open about us", Toast.LENGTH_SHORT).show();
    }
}