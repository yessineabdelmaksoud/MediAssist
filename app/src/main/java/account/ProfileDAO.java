package account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProfileDAO {
    private SQLiteDatabase database;
    private ProfileDatabaseHelper dbHelper;

    public ProfileDAO(Context context) {
        dbHelper = new ProfileDatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Create or update a user profile
    public long saveProfile(UserProfile profile) {
        ContentValues values = new ContentValues();
        values.put(ProfileDatabaseHelper.COLUMN_USERNAME, profile.getUsername());
        values.put(ProfileDatabaseHelper.COLUMN_AGE, profile.getAge());
        values.put(ProfileDatabaseHelper.COLUMN_GENDER, profile.getGender());
        values.put(ProfileDatabaseHelper.COLUMN_BLOOD_TYPE, profile.getBloodType());
        values.put(ProfileDatabaseHelper.COLUMN_WEIGHT, profile.getWeight());
        values.put(ProfileDatabaseHelper.COLUMN_HEIGHT, profile.getHeight());
        values.put(ProfileDatabaseHelper.COLUMN_PHONE, profile.getPhone());
        values.put(ProfileDatabaseHelper.COLUMN_ADDRESS, profile.getAddress());
        values.put(ProfileDatabaseHelper.COLUMN_MEDICAL_CONDITIONS, profile.getMedicalConditions());
        values.put(ProfileDatabaseHelper.COLUMN_PROFILE_IMAGE, profile.getProfileImagePath());

        // Check if profile already exists
        Cursor cursor = database.query(
                ProfileDatabaseHelper.TABLE_PROFILES,
                new String[]{ProfileDatabaseHelper.COLUMN_ID},
                ProfileDatabaseHelper.COLUMN_USERNAME + " = ?",
                new String[]{profile.getUsername()},
                null, null, null
        );

        long insertId;

        if (cursor != null && cursor.moveToFirst()) {
            // Update existing profile
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_ID));
            insertId = database.update(
                    ProfileDatabaseHelper.TABLE_PROFILES,
                    values,
                    ProfileDatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
            cursor.close();
        } else {
            // Insert new profile
            insertId = database.insert(ProfileDatabaseHelper.TABLE_PROFILES, null, values);
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return insertId;
    }

    // Get profile by username
    public UserProfile getProfileByUsername(String username) {
        UserProfile profile = null;

        Cursor cursor = database.query(
                ProfileDatabaseHelper.TABLE_PROFILES,
                null,
                ProfileDatabaseHelper.COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            profile = cursorToProfile(cursor);
            cursor.close();
        }

        return profile;
    }

    // Delete profile
    public void deleteProfile(String username) {
        database.delete(
                ProfileDatabaseHelper.TABLE_PROFILES,
                ProfileDatabaseHelper.COLUMN_USERNAME + " = ?",
                new String[]{username}
        );
    }

    // Convert cursor to UserProfile object
    private UserProfile cursorToProfile(Cursor cursor) {
        UserProfile profile = new UserProfile();

        profile.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_ID)));
        profile.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_USERNAME)));

        int ageIndex = cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_AGE);
        if (!cursor.isNull(ageIndex)) {
            profile.setAge(cursor.getInt(ageIndex));
        }

        profile.setGender(cursor.getString(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_GENDER)));
        profile.setBloodType(cursor.getString(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_BLOOD_TYPE)));

        int weightIndex = cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_WEIGHT);
        if (!cursor.isNull(weightIndex)) {
            profile.setWeight(cursor.getFloat(weightIndex));
        }

        int heightIndex = cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_HEIGHT);
        if (!cursor.isNull(heightIndex)) {
            profile.setHeight(cursor.getFloat(heightIndex));
        }

        profile.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_PHONE)));
        profile.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_ADDRESS)));
        profile.setMedicalConditions(cursor.getString(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_MEDICAL_CONDITIONS)));
        profile.setProfileImagePath(cursor.getString(cursor.getColumnIndexOrThrow(ProfileDatabaseHelper.COLUMN_PROFILE_IMAGE)));

        return profile;
    }
}