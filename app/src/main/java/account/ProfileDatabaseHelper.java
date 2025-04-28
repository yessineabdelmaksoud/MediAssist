package account;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfileDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "user_profiles.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_PROFILES = "profiles";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_BLOOD_TYPE = "blood_type";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_MEDICAL_CONDITIONS = "medical_conditions";
    public static final String COLUMN_PROFILE_IMAGE = "profile_image";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_PROFILES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_USERNAME + " TEXT UNIQUE," +
                    COLUMN_AGE + " INTEGER," +
                    COLUMN_GENDER + " TEXT," +
                    COLUMN_BLOOD_TYPE + " TEXT," +
                    COLUMN_WEIGHT + " REAL," +
                    COLUMN_HEIGHT + " REAL," +
                    COLUMN_PHONE + " TEXT," +
                    COLUMN_ADDRESS + " TEXT," +
                    COLUMN_MEDICAL_CONDITIONS + " TEXT," +
                    COLUMN_PROFILE_IMAGE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_PROFILES;

    public ProfileDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}