package account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.applicationproject.R;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {

    private ImageView backButton;
    private Button editProfileButton;
    private ImageView profileImage;
    private TextView usernameValue, ageValue, genderValue, bloodTypeValue, weightValue, heightValue, phoneValue, addressValue, medicalConditionsValue;

    private ProfileDAO profileDAO;
    private UserProfile userProfile;
    private String username;
    private static final String PREFS_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        backButton = findViewById(R.id.backButton12);
        editProfileButton = findViewById(R.id.editProfileButton1);
        profileImage = findViewById(R.id.profileImage1);
        usernameValue = findViewById(R.id.usernameValue1);
        ageValue = findViewById(R.id.ageValue);
        genderValue = findViewById(R.id.genderValue);
        bloodTypeValue = findViewById(R.id.bloodTypeValue);
        weightValue = findViewById(R.id.weightValue);
        heightValue = findViewById(R.id.heightValue);
        phoneValue = findViewById(R.id.phoneValue);
        addressValue = findViewById(R.id.addressValue);
        medicalConditionsValue = findViewById(R.id.medicalConditionsValue);

        // Get username from shared preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        username = settings.getString("username", "");

        // Initialize database access
        profileDAO = new ProfileDAO(this);
        profileDAO.open();

        // Load user profile
        loadUserProfile();

        // Set button click listeners
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload user profile data each time activity resumes
        loadUserProfile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileDAO.close();
    }

    private void loadUserProfile() {
        // Get user profile from database
        userProfile = profileDAO.getProfileByUsername(username);

        // If profile exists, populate UI with data
        if (userProfile != null) {
            usernameValue.setText(userProfile.getUsername());

            if (userProfile.getAge() > 0) {
                ageValue.setText(String.valueOf(userProfile.getAge()));
            } else {
                ageValue.setText("Non spécifié");
            }

            if (userProfile.getGender() != null && !userProfile.getGender().isEmpty()) {
                genderValue.setText(userProfile.getGender());
            } else {
                genderValue.setText("Non spécifié");
            }

            if (userProfile.getBloodType() != null && !userProfile.getBloodType().isEmpty()) {
                bloodTypeValue.setText(userProfile.getBloodType());
            } else {
                bloodTypeValue.setText("Non spécifié");
            }

            if (userProfile.getWeight() > 0) {
                weightValue.setText(String.format("%.1f", userProfile.getWeight()));
            } else {
                weightValue.setText("Non spécifié");
            }

            if (userProfile.getHeight() > 0) {
                heightValue.setText(String.format("%.1f", userProfile.getHeight()));
            } else {
                heightValue.setText("Non spécifié");
            }

            if (userProfile.getPhone() != null && !userProfile.getPhone().isEmpty()) {
                phoneValue.setText(userProfile.getPhone());
            } else {
                phoneValue.setText("Non spécifié");
            }

            if (userProfile.getAddress() != null && !userProfile.getAddress().isEmpty()) {
                addressValue.setText(userProfile.getAddress());
            } else {
                addressValue.setText("Non spécifié");
            }

            if (userProfile.getMedicalConditions() != null && !userProfile.getMedicalConditions().isEmpty()) {
                medicalConditionsValue.setText(userProfile.getMedicalConditions());
            } else {
                medicalConditionsValue.setText("Non spécifié");
            }

            // Load profile image if it exists
            if (userProfile.getProfileImagePath() != null && !userProfile.getProfileImagePath().isEmpty()) {
                try {
                    File imgFile = new File(userProfile.getProfileImagePath());
                    if (imgFile.exists()) {
                        Uri photoUri = FileProvider.getUriForFile(this,
                                "com.example.applicationproject.fileprovider",
                                imgFile);
                        profileImage.setImageURI(photoUri);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Afficher une image par défaut
                }
            }
        } else {
            // Create new profile with just the username if it doesn't exist
            userProfile = new UserProfile(username);
            profileDAO.saveProfile(userProfile);
            usernameValue.setText(username);
        }
    }
}