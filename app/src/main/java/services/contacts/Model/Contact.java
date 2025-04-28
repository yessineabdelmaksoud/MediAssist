package services.contacts.Model;

import android.net.Uri;

public class Contact {
    private long id;
    private String name;
    private String phoneNumber;
    private String type;
    private Uri imageUri;
    private boolean isDefault;

    // Constructor with ID for existing contacts
    public Contact(long id, String name, String phoneNumber, String type, Uri imageUri, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.imageUri = imageUri;
        this.isDefault = isDefault;
    }

    // Constructor without ID for new contacts
    public Contact(String name, String phoneNumber, String type, Uri imageUri, boolean isDefault) {
        this.id = -1; // Temporary ID until saved to database
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.imageUri = imageUri;
        this.isDefault = isDefault;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}