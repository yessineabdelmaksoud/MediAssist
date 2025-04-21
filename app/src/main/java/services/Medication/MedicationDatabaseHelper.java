package services.Medication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MedicationDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "medications.db";
    public static final int DATABASE_VERSION = 2;

    public static final String COLUMN_PHOTO_PATH = "photo_path";

    public static final String TABLE_MEDICATIONS = "medications";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_POSOLOGIE = "posologie";
    public static final String COLUMN_FREQUENCE = "frequence";
    public static final String COLUMN_HEURE = "heure";
    public static final String COLUMN_REMARQUE = "remarque";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_MEDICATIONS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_NOM + " TEXT NOT NULL, " +
                    COLUMN_POSOLOGIE + " TEXT, " +
                    COLUMN_FREQUENCE + " TEXT, " +
                    COLUMN_HEURE + " TEXT, " +
                    COLUMN_REMARQUE + " TEXT," +
                    COLUMN_PHOTO_PATH + " TEXT" +
                    ")";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_MEDICATIONS;

    public MedicationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Ajouter la colonne photo_path à la table existante
            db.execSQL("ALTER TABLE " + TABLE_MEDICATIONS +
                    " ADD COLUMN " + COLUMN_PHOTO_PATH + " TEXT");
        } else {
            db.execSQL(SQL_DELETE_TABLE);
            onCreate(db);
        }
    }
}