package services.Medicaments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MedicamentDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medicaments.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    public static final String TABLE_MEDICAMENTS = "medicaments";
    public static final String TABLE_ALARMES = "alarmes";
    public static final String TABLE_JOURS = "jours";

    // Common column
    public static final String COLUMN_ID = "_id";

    // Medicaments table columns
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_POSOLOGIE = "posologie";
    public static final String COLUMN_FREQUENCE = "frequence";
    public static final String COLUMN_REMARQUE = "remarque";
    public static final String COLUMN_IMAGE_PATH = "image_path";

    // Alarmes table columns
    public static final String COLUMN_MEDICAMENT_ID = "medicament_id";
    public static final String COLUMN_HEURE = "heure";

    // Jours table columns
    public static final String COLUMN_JOUR = "jour";

    // Create table statements
    private static final String CREATE_TABLE_MEDICAMENTS =
            "CREATE TABLE " + TABLE_MEDICAMENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOM + " TEXT NOT NULL, " +
                    COLUMN_POSOLOGIE + " TEXT NOT NULL, " +
                    COLUMN_FREQUENCE + " TEXT NOT NULL, " +
                    COLUMN_REMARQUE + " TEXT, " +
                    COLUMN_IMAGE_PATH + " TEXT);";

    private static final String CREATE_TABLE_ALARMES =
            "CREATE TABLE " + TABLE_ALARMES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MEDICAMENT_ID + " INTEGER NOT NULL, " +
                    COLUMN_HEURE + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_MEDICAMENT_ID + ") REFERENCES " +
                    TABLE_MEDICAMENTS + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

    private static final String CREATE_TABLE_JOURS =
            "CREATE TABLE " + TABLE_JOURS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MEDICAMENT_ID + " INTEGER NOT NULL, " +
                    COLUMN_JOUR + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_MEDICAMENT_ID + ") REFERENCES " +
                    TABLE_MEDICAMENTS + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

    public MedicamentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEDICAMENTS);
        db.execSQL(CREATE_TABLE_ALARMES);
        db.execSQL(CREATE_TABLE_JOURS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICAMENTS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}