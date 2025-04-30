package account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import database.AppDatabaseHelper;

public class ProfileDAO {
    private SQLiteDatabase database;
    private AppDatabaseHelper dbHelper;

    public ProfileDAO(Context context) {
        dbHelper = new AppDatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Get user ID from username
    private int getUserIdFromUsername(String username) {
        int userId = -1;
        Cursor cursor = database.query(
                AppDatabaseHelper.TABLE_USERS,
                new String[]{AppDatabaseHelper.COLUMN_ID},
                AppDatabaseHelper.COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_ID));
            cursor.close();
        }
        return userId;
    }

    // Create or update a user profile
    public long saveProfile(UserProfile profile) {
        int userId = getUserIdFromUsername(profile.getUsername());

        if (userId == -1) {
            return -1; // User not found
        }

        ContentValues values = new ContentValues();
        values.put(AppDatabaseHelper.COLUMN_USER_ID, userId);
        values.put(AppDatabaseHelper.COLUMN_AGE, profile.getAge());
        values.put(AppDatabaseHelper.COLUMN_GENDER, profile.getGender());
        values.put(AppDatabaseHelper.COLUMN_BLOOD_TYPE, profile.getBloodType());
        values.put(AppDatabaseHelper.COLUMN_WEIGHT, profile.getWeight());
        values.put(AppDatabaseHelper.COLUMN_HEIGHT, profile.getHeight());
        values.put(AppDatabaseHelper.COLUMN_PHONE, profile.getPhone());
        values.put(AppDatabaseHelper.COLUMN_ADDRESS, profile.getAddress());
        values.put(AppDatabaseHelper.COLUMN_MEDICAL_CONDITIONS, profile.getMedicalConditions());
        values.put(AppDatabaseHelper.COLUMN_PROFILE_IMAGE, profile.getProfileImagePath());

        // Check if profile already exists
        Cursor cursor = database.query(
                AppDatabaseHelper.TABLE_PROFILES,
                new String[]{AppDatabaseHelper.COLUMN_PROFILE_ID},
                AppDatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        long insertId;

        if (cursor != null && cursor.moveToFirst()) {
            // Update existing profile
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_PROFILE_ID));
            insertId = database.update(
                    AppDatabaseHelper.TABLE_PROFILES,
                    values,
                    AppDatabaseHelper.COLUMN_PROFILE_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
            cursor.close();
        } else {
            // Insert new profile
            insertId = database.insert(AppDatabaseHelper.TABLE_PROFILES, null, values);
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return insertId;
    }

    // Get profile by username
    public UserProfile getProfileByUsername(String username) {
        UserProfile profile = null;
        int userId = getUserIdFromUsername(username);

        if (userId == -1) {
            return null; // User not found
        }

        Cursor cursor = database.query(
                AppDatabaseHelper.TABLE_PROFILES,
                null,
                AppDatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            profile = cursorToProfile(cursor);
            profile.setUsername(username); // Set username for backward compatibility
            cursor.close();
        }

        return profile;
    }

    // Delete profile
    public void deleteProfile(String username) {
        int userId = getUserIdFromUsername(username);

        if (userId != -1) {
            database.delete(
                    AppDatabaseHelper.TABLE_PROFILES,
                    AppDatabaseHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
        }
    }

    // Convert cursor to UserProfile object
    private UserProfile cursorToProfile(Cursor cursor) {
        UserProfile profile = new UserProfile();

        profile.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_PROFILE_ID)));
        profile.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_USER_ID)));

        int ageIndex = cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_AGE);
        if (!cursor.isNull(ageIndex)) {
            profile.setAge(cursor.getInt(ageIndex));
        }

        profile.setGender(cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_GENDER)));
        profile.setBloodType(cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_BLOOD_TYPE)));

        int weightIndex = cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_WEIGHT);
        if (!cursor.isNull(weightIndex)) {
            profile.setWeight(cursor.getFloat(weightIndex));
        }

        int heightIndex = cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_HEIGHT);
        if (!cursor.isNull(heightIndex)) {
            profile.setHeight(cursor.getFloat(heightIndex));
        }

        profile.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_PHONE)));
        profile.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_ADDRESS)));
        profile.setMedicalConditions(cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_MEDICAL_CONDITIONS)));
        profile.setProfileImagePath(cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_PROFILE_IMAGE)));

        return profile;
    }
}