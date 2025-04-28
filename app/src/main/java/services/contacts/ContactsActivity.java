package services.contacts;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.HomeActivity;
import com.example.applicationproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import services.Appointement.ApptsActivity;
import services.Medicaments.medicament_activity;
import services.contacts.Model.Contact;
import services.prescriptions.PrescriptionsActivity;

public class ContactsActivity extends AppCompatActivity implements ContactAdapter.OnContactClickListener {

    private static final int REQUEST_ADD_CONTACT = 1;
    private static final int REQUEST_EDIT_CONTACT = 2;

    private RecyclerView contactsRecyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactsList;
    private List<Contact> filteredContactsList;
    private EditText searchEditText;
    private ContactDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Initialize database helper
        databaseHelper = new ContactDatabaseHelper(this);

        // Initialize UI components
        contactsRecyclerView = findViewById(R.id.contactsRecyclerView);
        searchEditText = findViewById(R.id.searchContacts);
        MaterialButton addButton = findViewById(R.id.addButton);

        // Set up contact list
        setupContactsList();

        // Set up search functionality
        setupSearch();

        // Add button click listener
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ContactsActivity.this, AddContactActivity.class);
            startActivityForResult(intent, REQUEST_ADD_CONTACT);
        });

        // Set up bottom navigation
        setupBottomNavigation();
    }

    private void setupContactsList() {
        // Initialize lists
        contactsList = new ArrayList<>();
        filteredContactsList = new ArrayList<>();

        // Load data from database
        loadContactsFromDatabase();

        // Set up adapter
        contactAdapter = new ContactAdapter(filteredContactsList, this);
        contactsRecyclerView.setAdapter(contactAdapter);
    }

    private void loadContactsFromDatabase() {
        contactsList.clear();
        contactsList.addAll(databaseHelper.getAllContacts());

        filteredContactsList.clear();
        filteredContactsList.addAll(contactsList);

        if (contactAdapter != null) {
            contactAdapter.notifyDataSetChanged();
        }
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterContacts(String query) {
        filteredContactsList.clear();

        if (query.isEmpty()) {
            filteredContactsList.addAll(contactsList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Contact contact : contactsList) {
                if (contact.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredContactsList.add(contact);
                }
            }
        }

        contactAdapter.notifyDataSetChanged();
    }

    private void setupBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_contacts);

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
                startActivity(new Intent(this, PrescriptionsActivity.class));
                return true;
            } else if (itemId == R.id.nav_contacts) {
                // Current page
                return true;
            }
            return false;
        });
    }

    @Override
    public void onCallButtonClick(String phoneNumber) {
        // Handle call button click
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    @Override
    public void onEditButtonClick(Contact contact) {
        // Handle edit button click
        Intent intent = new Intent(ContactsActivity.this, AddContactActivity.class);
        intent.putExtra("contact_id", contact.getId());
        intent.putExtra("is_edit_mode", true);
        startActivityForResult(intent, REQUEST_EDIT_CONTACT);
    }

    @Override
    public void onDeleteButtonClick(Contact contact) {
        // Handle delete button click with confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete " + contact.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete from database
                    databaseHelper.deleteContact(contact.getId());

                    // Remove from lists and update UI
                    contactsList.remove(contact);
                    filteredContactsList.remove(contact);
                    contactAdapter.notifyDataSetChanged();

                    Toast.makeText(ContactsActivity.this, "Contact deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_ADD_CONTACT || requestCode == REQUEST_EDIT_CONTACT) &&
                resultCode == RESULT_OK) {
            // Reload contacts from database
            loadContactsFromDatabase();
            Toast.makeText(this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh contacts list whenever the activity resumes
        loadContactsFromDatabase();
    }
}