package services.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import services.contacts.Model.Contact;

public class ContactDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "emergency_contacts.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    public static final String TABLE_CONTACTS = "contacts";

    // Column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_IMAGE_URI = "image_uri";
    public static final String COLUMN_IS_DEFAULT = "is_default";

    // Create table query
    private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_PHONE + " TEXT NOT NULL, "
            + COLUMN_TYPE + " TEXT NOT NULL, "
            + COLUMN_IMAGE_URI + " TEXT, "
            + COLUMN_IS_DEFAULT + " INTEGER DEFAULT 0)";

    public ContactDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    // CRUD Operations

    // Add a new contact
    public long addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_PHONE, contact.getPhoneNumber());
        values.put(COLUMN_TYPE, contact.getType());
        values.put(COLUMN_IMAGE_URI, contact.getImageUri() != null ? contact.getImageUri().toString() : null);
        values.put(COLUMN_IS_DEFAULT, contact.isDefault() ? 1 : 0);

        // If this contact is set as default, make sure no other contacts are default
        if (contact.isDefault()) {
            clearDefaultContact(db);
        }

        long id = db.insert(TABLE_CONTACTS, null, values);
        contact.setId(id);

        db.close();
        return id;
    }

    // Get a single contact by ID
    public Contact getContact(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_CONTACTS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE, COLUMN_TYPE, COLUMN_IMAGE_URI, COLUMN_IS_DEFAULT},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        Contact contact = null;
        if (cursor != null && cursor.moveToFirst()) {
            String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI));
            Uri imageUri = null;

            if (imageUriString != null) {
                try {
                    imageUri = Uri.parse(imageUriString);
                } catch (Exception e) {
                    Log.e("ContactDatabaseHelper", "Error parsing URI: " + e.getMessage());
                }
            }

            contact = new Contact(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                    imageUri,
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DEFAULT)) == 1
            );
            cursor.close();
        }

        db.close();
        return contact;
    }


    // Get all contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS + " ORDER BY " + COLUMN_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI));
                Uri imageUri = null;

                if (imageUriString != null) {
                    try {
                        imageUri = Uri.parse(imageUriString);
                    } catch (Exception e) {
                        Log.e("ContactDatabaseHelper", "Error parsing URI: " + e.getMessage());
                    }
                }

                Contact contact = new Contact(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                        imageUri,
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DEFAULT)) == 1
                );
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return contactList;
    }

    // Update contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_PHONE, contact.getPhoneNumber());
        values.put(COLUMN_TYPE, contact.getType());
        values.put(COLUMN_IMAGE_URI, contact.getImageUri() != null ? contact.getImageUri().toString() : null);
        values.put(COLUMN_IS_DEFAULT, contact.isDefault() ? 1 : 0);

        // If this contact is set as default, make sure no other contacts are default
        if (contact.isDefault()) {
            clearDefaultContact(db);
        }

        // Update row
        int result = db.update(TABLE_CONTACTS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});

        db.close();
        return result;
    }

    // Delete contact
    public void deleteContact(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Get contact count
    public int getContactsCount() {
        String countQuery = "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // Clear default contact
    private void clearDefaultContact(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_DEFAULT, 0);
        db.update(TABLE_CONTACTS, values, COLUMN_IS_DEFAULT + "=?", new String[]{"1"});
    }
}