package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "health_app.db";
    public static final int DATABASE_VERSION = 1;

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

    private static final String SQL_DELETE_USERS =
            "DROP TABLE IF EXISTS " + TABLE_USERS;

    private static final String SQL_DELETE_PROFILES =
            "DROP TABLE IF EXISTS " + TABLE_PROFILES;

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_PROFILES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Supprimer d'abord la table profiles car elle a une clé étrangère
        db.execSQL(SQL_DELETE_PROFILES);
        db.execSQL(SQL_DELETE_USERS);
        onCreate(db);
    }

    // Méthode de migration des données depuis les anciennes bases
    public void migrateData(Context context, SQLiteDatabase newDb) {
        // Migration depuis les anciennes bases de données
        // (Cette méthode sera implémentée dans une classe séparée)
    }
}