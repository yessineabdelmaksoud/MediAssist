package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "health_app.db";
    public static final int DATABASE_VERSION = 2; // Incrémentez la version de la base de données

    // Table Users (Login info)
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USERNAME = "username";

    // Table Profiles (User profile info)
    public static final String TABLE_PROFILES = "profiles";
    public static final String COLUMN_PROFILE_ID = "_id";
    public static final String COLUMN_USER_ID = "user_id";  // Foreign key to users table
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_BLOOD_TYPE = "blood_type";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_MEDICAL_CONDITIONS = "medical_conditions";
    public static final String COLUMN_PROFILE_IMAGE = "profile_image";

    // Table Medicaments
    public static final String TABLE_MEDICAMENTS = "medicaments";
    public static final String COLUMN_MEDICAMENT_ID = "_id"; // Correction ici
    public static final String COLUMN_MEDICAMENT_USER_ID = "user_id"; // Foreign key to users table
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_POSOLOGIE = "posologie";
    public static final String COLUMN_FREQUENCE = "frequence";
    public static final String COLUMN_REMARQUE = "remarque";
    public static final String COLUMN_IMAGE_PATH = "image_path";

    // Table Alarmes
    public static final String TABLE_ALARMES = "alarmes";
    public static final String COLUMN_ALARM_ID = "_id";
    public static final String COLUMN_MEDICAMENT_ID_ALARM = "medicament_id"; // Renamed to avoid conflict
    public static final String COLUMN_HEURE = "heure";

    // Table Jours
    public static final String TABLE_JOURS = "jours";
    public static final String COLUMN_JOUR_ID = "_id";
    public static final String COLUMN_JOUR = "jour";

    private static final String SQL_CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_EMAIL + " TEXT," +
                    COLUMN_PASSWORD + " TEXT," +
                    COLUMN_USERNAME + " TEXT UNIQUE)";

    private static final String SQL_CREATE_PROFILES_TABLE =
            "CREATE TABLE " + TABLE_PROFILES + " (" +
                    COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_USER_ID + " INTEGER NOT NULL," +
                    COLUMN_AGE + " INTEGER," +
                    COLUMN_GENDER + " TEXT," +
                    COLUMN_BLOOD_TYPE + " TEXT," +
                    COLUMN_WEIGHT + " REAL," +
                    COLUMN_HEIGHT + " REAL," +
                    COLUMN_PHONE + " TEXT," +
                    COLUMN_ADDRESS + " TEXT," +
                    COLUMN_MEDICAL_CONDITIONS + " TEXT," +
                    COLUMN_PROFILE_IMAGE + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_ID + "))";

    private static final String SQL_CREATE_MEDICAMENTS_TABLE =
            "CREATE TABLE " + TABLE_MEDICAMENTS + " (" +
                    COLUMN_MEDICAMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MEDICAMENT_USER_ID + " INTEGER NOT NULL, " +
                    COLUMN_NOM + " TEXT NOT NULL, " +
                    COLUMN_POSOLOGIE + " TEXT NOT NULL, " +
                    COLUMN_FREQUENCE + " TEXT NOT NULL, " +
                    COLUMN_REMARQUE + " TEXT, " +
                    COLUMN_IMAGE_PATH + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_MEDICAMENT_USER_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

    private static final String SQL_CREATE_ALARMES_TABLE =
            "CREATE TABLE " + TABLE_ALARMES + " (" +
                    COLUMN_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MEDICAMENT_ID_ALARM + " INTEGER NOT NULL, " +
                    COLUMN_HEURE + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_MEDICAMENT_ID_ALARM + ") REFERENCES " +
                    TABLE_MEDICAMENTS + "(" + COLUMN_MEDICAMENT_ID + ") ON DELETE CASCADE);";

    private static final String SQL_CREATE_JOURS_TABLE =
            "CREATE TABLE " + TABLE_JOURS + " (" +
                    COLUMN_JOUR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MEDICAMENT_ID + " INTEGER NOT NULL, " +
                    COLUMN_JOUR + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_MEDICAMENT_ID + ") REFERENCES " +
                    TABLE_MEDICAMENTS + "(" + COLUMN_MEDICAMENT_ID + ") ON DELETE CASCADE);";

    private static final String SQL_DELETE_USERS =
            "DROP TABLE IF EXISTS " + TABLE_USERS;

    private static final String SQL_DELETE_PROFILES =
            "DROP TABLE IF EXISTS " + TABLE_PROFILES;

    private static final String SQL_DELETE_MEDICAMENTS =
            "DROP TABLE IF EXISTS " + TABLE_MEDICAMENTS;

    private static final String SQL_DELETE_ALARMES =
            "DROP TABLE IF EXISTS " + TABLE_ALARMES;

    private static final String SQL_DELETE_JOURS =
            "DROP TABLE IF EXISTS " + TABLE_JOURS;

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_PROFILES_TABLE);
        db.execSQL(SQL_CREATE_MEDICAMENTS_TABLE);
        db.execSQL(SQL_CREATE_ALARMES_TABLE);
        db.execSQL(SQL_CREATE_JOURS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_JOURS);
        db.execSQL(SQL_DELETE_ALARMES);
        db.execSQL(SQL_DELETE_MEDICAMENTS);
        db.execSQL(SQL_DELETE_PROFILES);
        db.execSQL(SQL_DELETE_USERS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
