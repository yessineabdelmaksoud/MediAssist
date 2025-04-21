package services.Medication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class MedicationRepository {

    private MedicationDatabaseHelper dbHelper;

    public MedicationRepository(Context context) {
        dbHelper = new MedicationDatabaseHelper(context);
    }

    // ➕ Ajouter un médicament
    public long addMedication(Medication med) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", med.getUserId());
        values.put("nom", med.getNom());
        values.put("posologie", med.getPosologie());
        values.put("frequence", med.getFrequence());
        values.put("heure", med.getHeure());
        values.put("remarque", med.getRemarque());
        values.put("photo_path", med.getPhotoPath());

        return db.insert("medications", null, values);
    }

    // 🔄 Modifier un médicament
    public int updateMedication(Medication med) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nom", med.getNom());
        values.put("posologie", med.getPosologie());
        values.put("frequence", med.getFrequence());
        values.put("heure", med.getHeure());
        values.put("remarque", med.getRemarque());
        values.put("photo_path", med.getPhotoPath());

        return db.update("medications", values, "_id = ?", new String[]{String.valueOf(med.getId())});
    }

    // ❌ Supprimer un médicament
    public int deleteMedication(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("medications", "_id = ?", new String[]{String.valueOf(id)});
    }

    // 📄 Récupérer tous les médicaments d’un utilisateur
    public List<Medication> getAllMedications(int userId) {
        List<Medication> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "medications",
                null,
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "heure ASC" // tri par heure
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String nom = cursor.getString(cursor.getColumnIndexOrThrow("nom"));
                String posologie = cursor.getString(cursor.getColumnIndexOrThrow("posologie"));
                String frequence = cursor.getString(cursor.getColumnIndexOrThrow("frequence"));
                String heure = cursor.getString(cursor.getColumnIndexOrThrow("heure"));
                String remarque = cursor.getString(cursor.getColumnIndexOrThrow("remarque"));
                String photoPath = cursor.getString(cursor.getColumnIndexOrThrow("photo_path")); // Récupération du chemin


                Medication med = new Medication(id, userId, nom, posologie, frequence, heure, remarque , photoPath);
                list.add(med);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
    public Medication getMedicationById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "medications",
                null,
                "_id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            String nom = cursor.getString(cursor.getColumnIndexOrThrow("nom"));
            String posologie = cursor.getString(cursor.getColumnIndexOrThrow("posologie"));
            String frequence = cursor.getString(cursor.getColumnIndexOrThrow("frequence"));
            String heure = cursor.getString(cursor.getColumnIndexOrThrow("heure"));
            String remarque = cursor.getString(cursor.getColumnIndexOrThrow("remarque"));
            String photoPath = cursor.getString(cursor.getColumnIndexOrThrow("photo_path")); // Récupération du chemin


            cursor.close();
            return new Medication(id, userId, nom, posologie, frequence, heure, remarque, photoPath);
        }

        return null;
    }


}
