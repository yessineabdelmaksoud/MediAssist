package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class DatabaseMigrationHelper {
    private static final String TAG = "DatabaseMigration";

    private Context context;

    public DatabaseMigrationHelper(Context context) {
        this.context = context;
    }

    public void migrateData() {
        // Créer une instance de la nouvelle base de données
        AppDatabaseHelper newDbHelper = new AppDatabaseHelper(context);
        SQLiteDatabase newDb = newDbHelper.getWritableDatabase();

        // Migration des utilisateurs
        migrateUsers(newDb);

        // Migration des profils
        migrateProfiles(newDb);

        newDb.close();
    }

    private void migrateUsers(SQLiteDatabase newDb) {
        try {
            // Ouvrir l'ancienne base de données des utilisateurs
            login.DatabaseHelper oldDbHelper = new login.DatabaseHelper(context);
            SQLiteDatabase oldDb = oldDbHelper.getReadableDatabase();

            // Lire tous les utilisateurs
            Cursor cursor = oldDb.query(
                    login.DatabaseHelper.TABLE_USERS,
                    null, // toutes les colonnes
                    null, // pas de WHERE clause
                    null, // pas de WHERE arguments
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ContentValues values = new ContentValues();
                    values.put(AppDatabaseHelper.COLUMN_ID,
                            cursor.getInt(cursor.getColumnIndexOrThrow(login.DatabaseHelper.COLUMN_ID)));
                    values.put(AppDatabaseHelper.COLUMN_NAME,
                            cursor.getString(cursor.getColumnIndexOrThrow(login.DatabaseHelper.COLUMN_NAME)));
                    values.put(AppDatabaseHelper.COLUMN_EMAIL,
                            cursor.getString(cursor.getColumnIndexOrThrow(login.DatabaseHelper.COLUMN_EMAIL)));
                    values.put(AppDatabaseHelper.COLUMN_PASSWORD,
                            cursor.getString(cursor.getColumnIndexOrThrow(login.DatabaseHelper.COLUMN_PASSWORD)));
                    values.put(AppDatabaseHelper.COLUMN_USERNAME,
                            cursor.getString(cursor.getColumnIndexOrThrow(login.DatabaseHelper.COLUMN_USERNAME)));

                    // Insérer dans la nouvelle base de données
                    newDb.insert(AppDatabaseHelper.TABLE_USERS, null, values);

                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

            oldDb.close();
            oldDbHelper.close();

            Log.d(TAG, "Migration des utilisateurs terminée");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la migration des utilisateurs", e);
        }
    }

    private void migrateProfiles(SQLiteDatabase newDb) {
        try {
            // Créer un mapping des noms d'utilisateur aux ID
            Map<String, Integer> userIdMap = getUserIdMap(newDb);

            // Ouvrir l'ancienne base de données des profils
            account.ProfileDatabaseHelper oldDbHelper = new account.ProfileDatabaseHelper(context);
            SQLiteDatabase oldDb = oldDbHelper.getReadableDatabase();

            // Lire tous les profils
            Cursor cursor = oldDb.query(
                    account.ProfileDatabaseHelper.TABLE_PROFILES,
                    null, // toutes les colonnes
                    null, // pas de WHERE clause
                    null, // pas de WHERE arguments
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String username = cursor.getString(
                            cursor.getColumnIndexOrThrow(account.ProfileDatabaseHelper.COLUMN_USERNAME));

                    // Vérifier si on a l'ID utilisateur correspondant
                    Integer userId = userIdMap.get(username);
                    if (userId != null) {
                        ContentValues values = new ContentValues();
                        values.put(AppDatabaseHelper.COLUMN_USER_ID, userId);

                        // Colonnes optionnelles
                        int ageIdx = cursor.getColumnIndexOrThrow(account.ProfileDatabaseHelper.COLUMN_AGE);
                        if (!cursor.isNull(ageIdx)) {
                            values.put(AppDatabaseHelper.COLUMN_AGE, cursor.getInt(ageIdx));
                        }

                        int genderIdx = cursor.getColumnIndexOrThrow(account.ProfileDatabaseHelper.COLUMN_GENDER);
                        if (!cursor.isNull(genderIdx)) {
                            values.put(AppDatabaseHelper.COLUMN_GENDER, cursor.getString(genderIdx));
                        }

                        int bloodTypeIdx = cursor.getColumnIndexOrThrow(account.ProfileDatabaseHelper.COLUMN_BLOOD_TYPE);
                        if (!cursor.isNull(bloodTypeIdx)) {
                            values.put(AppDatabaseHelper.COLUMN_BLOOD_TYPE, cursor.getString(bloodTypeIdx));
                        }

                        int weightIdx = cursor.getColumnIndexOrThrow(account.ProfileDatabaseHelper.COLUMN_WEIGHT);
                        if (!cursor.isNull(weightIdx)) {
                            values.put(AppDatabaseHelper.COLUMN_WEIGHT, cursor.getFloat(weightIdx));
                        }

                        int heightIdx = cursor.getColumnIndexOrThrow(account.ProfileDatabaseHelper.COLUMN_HEIGHT);
                        if (!cursor.isNull(heightIdx)) {
                            values.put(AppDatabaseHelper.COLUMN_HEIGHT, cursor.getFloat(heightIdx));
                        }

                        int phoneIdx = cursor.getColumnIndexOrThrow(account.ProfileDatabaseHelper.COLUMN_PHONE);
                        if (!cursor.isNull(phoneIdx)) {
                            values.put(AppDatabaseHelper.COLUMN_PHONE, cursor.getString(phoneIdx));
                        }

                        int addressIdx = cursor.getColumnIndexOrThrow(account.ProfileDatabaseHelper.COLUMN_ADDRESS);
                        if (!cursor.isNull(addressIdx)) {
                            values.put(AppDatabaseHelper.COLUMN_ADDRESS, cursor.getString(addressIdx));
                        }

                        int medCondIdx = cursor.getColumnIndexOrThrow(account.ProfileDatabaseHelper.COLUMN_MEDICAL_CONDITIONS);
                        if (!cursor.isNull(medCondIdx)) {
                            values.put(AppDatabaseHelper.COLUMN_MEDICAL_CONDITIONS, cursor.getString(medCondIdx));
                        }

                        int profileImgIdx = cursor.getColumnIndexOrThrow(account.ProfileDatabaseHelper.COLUMN_PROFILE_IMAGE);
                        if (!cursor.isNull(profileImgIdx)) {
                            values.put(AppDatabaseHelper.COLUMN_PROFILE_IMAGE, cursor.getString(profileImgIdx));
                        }

                        // Insérer dans la nouvelle base de données
                        newDb.insert(AppDatabaseHelper.TABLE_PROFILES, null, values);
                    }

                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

            oldDb.close();
            oldDbHelper.close();

            Log.d(TAG, "Migration des profils terminée");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la migration des profils", e);
        }
    }

    private Map<String, Integer> getUserIdMap(SQLiteDatabase db) {
        Map<String, Integer> userIdMap = new HashMap<>();

        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_USERS,
                new String[]{AppDatabaseHelper.COLUMN_ID, AppDatabaseHelper.COLUMN_USERNAME},
                null, null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_USERNAME));
                userIdMap.put(username, id);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return userIdMap;
    }
}