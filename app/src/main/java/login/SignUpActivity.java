package login;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applicationproject.R;

import database.AppDatabaseHelper;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, usernameEditText;
    private Button signupButton;
    private TextView loginText;
    private AppDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        usernameEditText = findViewById(R.id.username);
        signupButton = findViewById(R.id.signupButton);
        loginText = findViewById(R.id.loginText);

        dbHelper = new AppDatabaseHelper(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(AppDatabaseHelper.COLUMN_NAME, name);
                    values.put(AppDatabaseHelper.COLUMN_EMAIL, email);
                    values.put(AppDatabaseHelper.COLUMN_PASSWORD, password);
                    values.put(AppDatabaseHelper.COLUMN_USERNAME, username);

                    long newRowId = db.insert(AppDatabaseHelper.TABLE_USERS, null, values);
                    if (newRowId != -1) {
                        // Créer automatiquement un profil vide pour le nouvel utilisateur
                        ContentValues profileValues = new ContentValues();
                        profileValues.put(AppDatabaseHelper.COLUMN_USER_ID, newRowId);
                        db.insert(AppDatabaseHelper.TABLE_PROFILES, null, profileValues);

                        Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}