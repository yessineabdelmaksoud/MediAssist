package services.Medicaments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import database.AppDatabaseHelper;

public class MedicamentDAO {
    private SQLiteDatabase database;
    private AppDatabaseHelper dbHelper;
    private String[] allColumnsMedicament = {
            AppDatabaseHelper.COLUMN_MEDICAMENT_ID,
            AppDatabaseHelper.COLUMN_NOM,
            AppDatabaseHelper.COLUMN_POSOLOGIE,
            AppDatabaseHelper.COLUMN_FREQUENCE,
            AppDatabaseHelper.COLUMN_REMARQUE,
            AppDatabaseHelper.COLUMN_IMAGE_PATH
    };

    public MedicamentDAO(Context context) {
        dbHelper = new AppDatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Insertion d'un médicament avec ses heures et jours
    public long createMedicament(Medicament medicament, long userId) {
        ContentValues values = new ContentValues();
        values.put(AppDatabaseHelper.COLUMN_MEDICAMENT_USER_ID, userId);
        values.put(AppDatabaseHelper.COLUMN_NOM, medicament.getNom());
        values.put(AppDatabaseHelper.COLUMN_POSOLOGIE, medicament.getPosologie());
        values.put(AppDatabaseHelper.COLUMN_FREQUENCE, medicament.getFrequence());
        values.put(AppDatabaseHelper.COLUMN_REMARQUE, medicament.getRemarque());
        values.put(AppDatabaseHelper.COLUMN_IMAGE_PATH, medicament.getImagePath());

        long medicamentId = database.insert(AppDatabaseHelper.TABLE_MEDICAMENTS, null, values);

        // Insérer les heures
        for (String heure : medicament.getHeures()) {
            ContentValues heureValues = new ContentValues();
            heureValues.put(AppDatabaseHelper.COLUMN_MEDICAMENT_ID, medicamentId);
            heureValues.put(AppDatabaseHelper.COLUMN_HEURE, heure);
            database.insert(AppDatabaseHelper.TABLE_ALARMES, null, heureValues);
        }

        // Insérer les jours
        for (String jour : medicament.getJours()) {
            ContentValues jourValues = new ContentValues();
            jourValues.put(AppDatabaseHelper.COLUMN_MEDICAMENT_ID, medicamentId);
            jourValues.put(AppDatabaseHelper.COLUMN_JOUR, jour);
            database.insert(AppDatabaseHelper.TABLE_JOURS, null, jourValues);
        }

        return medicamentId;
    }

    // Mise à jour d'un médicament
    public int updateMedicament(Medicament medicament) {
        ContentValues values = new ContentValues();
        values.put(AppDatabaseHelper.COLUMN_NOM, medicament.getNom());
        values.put(AppDatabaseHelper.COLUMN_POSOLOGIE, medicament.getPosologie());
        values.put(AppDatabaseHelper.COLUMN_FREQUENCE, medicament.getFrequence());
        values.put(AppDatabaseHelper.COLUMN_REMARQUE, medicament.getRemarque());
        values.put(AppDatabaseHelper.COLUMN_IMAGE_PATH, medicament.getImagePath());

        // Supprimer les anciennes heures et jours
        database.delete(AppDatabaseHelper.TABLE_ALARMES,
                AppDatabaseHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(medicament.getId())});

        database.delete(AppDatabaseHelper.TABLE_JOURS,
                AppDatabaseHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(medicament.getId())});

        // Insérer les nouvelles heures
        for (String heure : medicament.getHeures()) {
            ContentValues heureValues = new ContentValues();
            heureValues.put(AppDatabaseHelper.COLUMN_MEDICAMENT_ID, medicament.getId());
            heureValues.put(AppDatabaseHelper.COLUMN_HEURE, heure);
            database.insert(AppDatabaseHelper.TABLE_ALARMES, null, heureValues);
        }

        // Insérer les nouveaux jours
        for (String jour : medicament.getJours()) {
            ContentValues jourValues = new ContentValues();
            jourValues.put(AppDatabaseHelper.COLUMN_MEDICAMENT_ID, medicament.getId());
            jourValues.put(AppDatabaseHelper.COLUMN_JOUR, jour);
            database.insert(AppDatabaseHelper.TABLE_JOURS, null, jourValues);
        }

        return database.update(AppDatabaseHelper.TABLE_MEDICAMENTS, values,
                AppDatabaseHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(medicament.getId())});
    }

    // Suppression d'un médicament
    public void deleteMedicament(long id) {
        database.delete(AppDatabaseHelper.TABLE_JOURS,
                AppDatabaseHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(id)});

        database.delete(AppDatabaseHelper.TABLE_ALARMES,
                AppDatabaseHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(id)});

        database.delete(AppDatabaseHelper.TABLE_MEDICAMENTS,
                AppDatabaseHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Obtenir un médicament par ID
    public Medicament getMedicamentById(long id) {
        Cursor cursor = database.query(AppDatabaseHelper.TABLE_MEDICAMENTS, allColumnsMedicament,
                AppDatabaseHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }

        Medicament medicament = cursorToMedicament(cursor);
        cursor.close();

        // Charger les heures
        medicament.setHeures(getHeuresByMedicamentId(id));

        // Charger les jours
        medicament.setJours(getJoursByMedicamentId(id));

        return medicament;
    }

    // Obtenir tous les médicaments pour un utilisateur donné
    public List<Medicament> getAllMedicaments(long userId) {
        List<Medicament> medicaments = new ArrayList<>();

        Cursor cursor = database.query(AppDatabaseHelper.TABLE_MEDICAMENTS,
                allColumnsMedicament, AppDatabaseHelper.COLUMN_MEDICAMENT_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null,
                AppDatabaseHelper.COLUMN_NOM + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Medicament medicament = cursorToMedicament(cursor);

            // Charger les heures
            medicament.setHeures(getHeuresByMedicamentId(medicament.getId()));

            // Charger les jours
            medicament.setJours(getJoursByMedicamentId(medicament.getId()));

            medicaments.add(medicament);
            cursor.moveToNext();
        }
        cursor.close();

        return medicaments;
    }

    // Rechercher des médicaments par nom pour un utilisateur donné
    public List<Medicament> searchMedicamentsByName(String query, long userId) {
        List<Medicament> medicaments = new ArrayList<>();

        Cursor cursor = database.query(AppDatabaseHelper.TABLE_MEDICAMENTS,
                allColumnsMedicament,
                AppDatabaseHelper.COLUMN_MEDICAMENT_USER_ID + " = ? AND " + AppDatabaseHelper.COLUMN_NOM + " LIKE ?",
                new String[]{String.valueOf(userId), "%" + query + "%"},
                null, null,
                AppDatabaseHelper.COLUMN_NOM + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Medicament medicament = cursorToMedicament(cursor);

            // Charger les heures
            medicament.setHeures(getHeuresByMedicamentId(medicament.getId()));

            // Charger les jours
            medicament.setJours(getJoursByMedicamentId(medicament.getId()));

            medicaments.add(medicament);
            cursor.moveToNext();
        }
        cursor.close();

        return medicaments;
    }

    // Méthodes utilitaires
    private Medicament cursorToMedicament(Cursor cursor) {
        Medicament medicament = new Medicament();
        medicament.setId(cursor.getLong(0));
        medicament.setNom(cursor.getString(1));
        medicament.setPosologie(cursor.getString(2));
        medicament.setFrequence(cursor.getString(3));
        medicament.setRemarque(cursor.getString(4));
        medicament.setImagePath(cursor.getString(5));
        return medicament;
    }

    private List<String> getHeuresByMedicamentId(long medicamentId) {
        List<String> heures = new ArrayList<>();

        Cursor cursor = database.query(AppDatabaseHelper.TABLE_ALARMES,
                new String[]{AppDatabaseHelper.COLUMN_HEURE},
                AppDatabaseHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(medicamentId)},
                null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            heures.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();

        return heures;
    }

    private List<String> getJoursByMedicamentId(long medicamentId) {
        List<String> jours = new ArrayList<>();

        Cursor cursor = database.query(AppDatabaseHelper.TABLE_JOURS,
                new String[]{AppDatabaseHelper.COLUMN_JOUR},
                AppDatabaseHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(medicamentId)},
                null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            jours.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();

        return jours;
    }
}
