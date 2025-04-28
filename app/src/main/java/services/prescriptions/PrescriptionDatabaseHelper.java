package services.prescriptions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "prescriptions.db";
    private static final int DATABASE_VERSION = 1;

    // Table et colonnes
    public static final String TABLE_PRESCRIPTIONS = "prescriptions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DOCTOR = "doctor";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_IMAGE_PATH = "image_path";

    // Requête SQL pour la création de la table
    private static final String CREATE_TABLE_PRESCRIPTIONS = "CREATE TABLE " + TABLE_PRESCRIPTIONS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DOCTOR + " TEXT, "
            + COLUMN_DATE + " TEXT, "
            + COLUMN_NOTE + " TEXT, "
            + COLUMN_IMAGE_PATH + " TEXT"
            + ")";

    public PrescriptionDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRESCRIPTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESCRIPTIONS);
        onCreate(db);
    }

    // Méthode pour ajouter une nouvelle prescription
    public long addPrescription(Prescription prescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, prescription.getTitle());
        values.put(COLUMN_DOCTOR, prescription.getDoctorName());
        values.put(COLUMN_DATE, prescription.getDate());
        values.put(COLUMN_NOTE, prescription.getNote());
        values.put(COLUMN_IMAGE_PATH, prescription.getImagePath());

        long id = db.insert(TABLE_PRESCRIPTIONS, null, values);
        db.close();
        return id;
    }

    // Méthode pour obtenir une prescription par son ID
    public Prescription getPrescription(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_PRESCRIPTIONS,
                new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_DOCTOR, COLUMN_DATE, COLUMN_NOTE, COLUMN_IMAGE_PATH},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            Prescription prescription = new Prescription(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
            );
            cursor.close();
            return prescription;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Méthode pour récupérer toutes les prescriptions
    public List<Prescription> getAllPrescriptions() {
        List<Prescription> prescriptionList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PRESCRIPTIONS + " ORDER BY " + COLUMN_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Prescription prescription = new Prescription(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
                );
                prescriptionList.add(prescription);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return prescriptionList;
    }

    // Méthode pour rechercher des prescriptions
    public List<Prescription> searchPrescriptions(String keyword) {
        List<Prescription> prescriptionList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PRESCRIPTIONS +
                " WHERE " + COLUMN_TITLE + " LIKE '%" + keyword + "%'" +
                " OR " + COLUMN_DOCTOR + " LIKE '%" + keyword + "%'" +
                " OR " + COLUMN_NOTE + " LIKE '%" + keyword + "%'" +
                " ORDER BY " + COLUMN_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Prescription prescription = new Prescription(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
                );
                prescriptionList.add(prescription);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return prescriptionList;
    }

    public int updatePrescription(Prescription prescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, prescription.getTitle());
        values.put(COLUMN_DOCTOR, prescription.getDoctorName());
        values.put(COLUMN_DATE, prescription.getDate());
        values.put(COLUMN_NOTE, prescription.getNote());
        values.put(COLUMN_IMAGE_PATH, prescription.getImagePath());

        int rowsAffected = db.update(
                TABLE_PRESCRIPTIONS,
                values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(prescription.getId())}
        );
        db.close();
        return rowsAffected;
    }

    // Méthode pour supprimer une prescription
    public void deletePrescription(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                TABLE_PRESCRIPTIONS,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        db.close();
    }

    // Méthode pour obtenir le nombre total de prescriptions
    public int getPrescriptionCount() {
        String countQuery = "SELECT * FROM " + TABLE_PRESCRIPTIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}