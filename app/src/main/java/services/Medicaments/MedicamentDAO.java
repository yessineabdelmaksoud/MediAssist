package services.Medicaments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MedicamentDAO {
    private SQLiteDatabase database;
    private MedicamentDbHelper dbHelper;
    private String[] allColumnsMedicament = {
            MedicamentDbHelper.COLUMN_ID,
            MedicamentDbHelper.COLUMN_NOM,
            MedicamentDbHelper.COLUMN_POSOLOGIE,
            MedicamentDbHelper.COLUMN_FREQUENCE,
            MedicamentDbHelper.COLUMN_REMARQUE,
            MedicamentDbHelper.COLUMN_IMAGE_PATH
    };

    public MedicamentDAO(Context context) {
        dbHelper = new MedicamentDbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Insertion d'un médicament avec ses heures et jours
    public long createMedicament(Medicament medicament) {
        ContentValues values = new ContentValues();
        values.put(MedicamentDbHelper.COLUMN_NOM, medicament.getNom());
        values.put(MedicamentDbHelper.COLUMN_POSOLOGIE, medicament.getPosologie());
        values.put(MedicamentDbHelper.COLUMN_FREQUENCE, medicament.getFrequence());
        values.put(MedicamentDbHelper.COLUMN_REMARQUE, medicament.getRemarque());
        values.put(MedicamentDbHelper.COLUMN_IMAGE_PATH, medicament.getImagePath());

        long medicamentId = database.insert(MedicamentDbHelper.TABLE_MEDICAMENTS, null, values);

        // Insérer les heures
        for (String heure : medicament.getHeures()) {
            ContentValues heureValues = new ContentValues();
            heureValues.put(MedicamentDbHelper.COLUMN_MEDICAMENT_ID, medicamentId);
            heureValues.put(MedicamentDbHelper.COLUMN_HEURE, heure);
            database.insert(MedicamentDbHelper.TABLE_ALARMES, null, heureValues);
        }

        // Insérer les jours
        for (String jour : medicament.getJours()) {
            ContentValues jourValues = new ContentValues();
            jourValues.put(MedicamentDbHelper.COLUMN_MEDICAMENT_ID, medicamentId);
            jourValues.put(MedicamentDbHelper.COLUMN_JOUR, jour);
            database.insert(MedicamentDbHelper.TABLE_JOURS, null, jourValues);
        }

        return medicamentId;
    }

    // Mise à jour d'un médicament
    public int updateMedicament(Medicament medicament) {
        ContentValues values = new ContentValues();
        values.put(MedicamentDbHelper.COLUMN_NOM, medicament.getNom());
        values.put(MedicamentDbHelper.COLUMN_POSOLOGIE, medicament.getPosologie());
        values.put(MedicamentDbHelper.COLUMN_FREQUENCE, medicament.getFrequence());
        values.put(MedicamentDbHelper.COLUMN_REMARQUE, medicament.getRemarque());
        values.put(MedicamentDbHelper.COLUMN_IMAGE_PATH, medicament.getImagePath());

        // Supprimer les anciennes heures et jours
        database.delete(MedicamentDbHelper.TABLE_ALARMES,
                MedicamentDbHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(medicament.getId())});

        database.delete(MedicamentDbHelper.TABLE_JOURS,
                MedicamentDbHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(medicament.getId())});

        // Insérer les nouvelles heures
        for (String heure : medicament.getHeures()) {
            ContentValues heureValues = new ContentValues();
            heureValues.put(MedicamentDbHelper.COLUMN_MEDICAMENT_ID, medicament.getId());
            heureValues.put(MedicamentDbHelper.COLUMN_HEURE, heure);
            database.insert(MedicamentDbHelper.TABLE_ALARMES, null, heureValues);
        }

        // Insérer les nouveaux jours
        for (String jour : medicament.getJours()) {
            ContentValues jourValues = new ContentValues();
            jourValues.put(MedicamentDbHelper.COLUMN_MEDICAMENT_ID, medicament.getId());
            jourValues.put(MedicamentDbHelper.COLUMN_JOUR, jour);
            database.insert(MedicamentDbHelper.TABLE_JOURS, null, jourValues);
        }

        return database.update(MedicamentDbHelper.TABLE_MEDICAMENTS, values,
                MedicamentDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(medicament.getId())});
    }

    // Suppression d'un médicament
    public void deleteMedicament(long id) {
        database.delete(MedicamentDbHelper.TABLE_JOURS,
                MedicamentDbHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(id)});

        database.delete(MedicamentDbHelper.TABLE_ALARMES,
                MedicamentDbHelper.COLUMN_MEDICAMENT_ID + " = ?",
                new String[]{String.valueOf(id)});

        database.delete(MedicamentDbHelper.TABLE_MEDICAMENTS,
                MedicamentDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Obtenir un médicament par ID
    public Medicament getMedicamentById(long id) {
        Cursor cursor = database.query(MedicamentDbHelper.TABLE_MEDICAMENTS, allColumnsMedicament,
                MedicamentDbHelper.COLUMN_ID + " = ?",
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

    // Obtenir tous les médicaments
    public List<Medicament> getAllMedicaments() {
        List<Medicament> medicaments = new ArrayList<>();

        Cursor cursor = database.query(MedicamentDbHelper.TABLE_MEDICAMENTS,
                allColumnsMedicament, null, null, null, null,
                MedicamentDbHelper.COLUMN_NOM + " ASC");

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

    // Rechercher des médicaments par nom
    public List<Medicament> searchMedicamentsByName(String query) {
        List<Medicament> medicaments = new ArrayList<>();

        Cursor cursor = database.query(MedicamentDbHelper.TABLE_MEDICAMENTS,
                allColumnsMedicament,
                MedicamentDbHelper.COLUMN_NOM + " LIKE ?",
                new String[]{"%" + query + "%"},
                null, null,
                MedicamentDbHelper.COLUMN_NOM + " ASC");

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

        Cursor cursor = database.query(MedicamentDbHelper.TABLE_ALARMES,
                new String[]{MedicamentDbHelper.COLUMN_HEURE},
                MedicamentDbHelper.COLUMN_MEDICAMENT_ID + " = ?",
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

        Cursor cursor = database.query(MedicamentDbHelper.TABLE_JOURS,
                new String[]{MedicamentDbHelper.COLUMN_JOUR},
                MedicamentDbHelper.COLUMN_MEDICAMENT_ID + " = ?",
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
