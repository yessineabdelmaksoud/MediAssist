package services.contacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.applicationproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;
import services.contacts.Model.Contact;
import services.contacts.utils.ImageUtils;

public class AddContactActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private CircleImageView contactImageView;
    private TextInputEditText nameInput;
    private TextInputEditText phoneInput;
    private AutoCompleteTextView typeInput;
    private SwitchMaterial defaultSwitch;
    private Uri selectedImageUri = null;

    private ContactDatabaseHelper databaseHelper;
    private boolean isEditMode = false;
    private long contactId = -1;
    private Contact currentContact = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Initialize database helper
        databaseHelper = new ContactDatabaseHelper(this);

        // Initialize UI components
        initializeViews();

        // Check if we're in edit mode
        handleIntentData();

        // Set up toolbar
        setupToolbar();

        // Set up contact types dropdown
        setupContactTypesDropdown();

        // Set up image selection
        setupImageSelection();

        // Set up save button
        setupSaveButton();
    }

    private void initializeViews() {
        contactImageView = findViewById(R.id.contactImageView);
        nameInput = findViewById(R.id.contactNameInput);
        phoneInput = findViewById(R.id.contactPhoneInput);
        typeInput = findViewById(R.id.contactTypeInput);
        defaultSwitch = findViewById(R.id.defaultContactSwitch);
    }
    // In AddContactActivity.java, update the handleIntentData method:

    private void handleIntentData() {
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("is_edit_mode", false);

        if (isEditMode) {
            contactId = intent.getLongExtra("contact_id", -1);
            if (contactId != -1) {
                // Load contact data
                currentContact = databaseHelper.getContact(contactId);
                if (currentContact != null) {
                    // Fill form with contact data
                    nameInput.setText(currentContact.getName());
                    phoneInput.setText(currentContact.getPhoneNumber());
                    typeInput.setText(currentContact.getType());
                    defaultSwitch.setChecked(currentContact.isDefault());

                    if (currentContact.getImageUri() != null) {
                        try {
                            selectedImageUri = currentContact.getImageUri();
                            contactImageView.setImageURI(selectedImageUri);
                        } catch (Exception e) {
                            Log.e("AddContactActivity", "Error loading image: " + e.getMessage());
                            contactImageView.setImageResource(R.drawable.default_profile);
                        }
                    }
                }
            }
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Update title based on mode
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
        if (toolbarTitle != null) {
            toolbarTitle.setText(isEditMode ? "Edit Contact" : "Add Emergency Contact");
        }
    }

    private void setupContactTypesDropdown() {
        String[] contactTypes = new String[]{
                "Primary Care", "Specialist", "Emergency", "Pharmacy",
                "Hospital", "Dentist", "Family Member", "Friend",
                "Medical Services", "Insurance", "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                contactTypes
        );

        typeInput.setAdapter(adapter);
    }

    private void setupImageSelection() {
        FloatingActionButton changePhotoButton = findViewById(R.id.changePhotoButton);
        changePhotoButton.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        });
    }

    private void setupSaveButton() {
        MaterialButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveContact();
            }
        });
    }

    private boolean validateInputs() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Name cannot be empty");
            return false;
        }

        if (phone.isEmpty()) {
            phoneInput.setError("Phone number cannot be empty");
            return false;
        }

        if (type.isEmpty()) {
            typeInput.setError("Please select a contact type");
            return false;
        }

        return true;
    }

    private void saveContact() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();
        boolean isDefault = defaultSwitch.isChecked();

        // Save the image to internal storage if one is selected
        Uri savedImageUri = null;
        if (selectedImageUri != null) {
            savedImageUri = ImageUtils.saveImageToInternalStorage(this, selectedImageUri);
        }

        Contact newContact = new Contact(name, phone, type, savedImageUri, isDefault);

        if (isEditMode && currentContact != null) {
            newContact.setId(currentContact.getId());
            int updated = databaseHelper.updateContact(newContact);
            if (updated > 0) {
                Toast.makeText(this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update contact", Toast.LENGTH_SHORT).show();
            }
        } else {
            long id = databaseHelper.addContact(newContact);
            if (id != -1) {
                Toast.makeText(this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save contact", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            contactImageView.setImageURI(selectedImageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}