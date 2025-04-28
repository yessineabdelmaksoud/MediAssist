package services.Appointement;
import services.Appointement.Appointment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appointments.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    public static final String TABLE_APPOINTMENTS = "appointments";

    // Column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DOCTOR = "doctor";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_PLACE = "place";
    public static final String COLUMN_AUTO_DELETE = "auto_delete";

    // Create table statement
    private static final String CREATE_TABLE_APPOINTMENTS = "CREATE TABLE " + TABLE_APPOINTMENTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DOCTOR + " TEXT NOT NULL, "
            + COLUMN_DATE + " TEXT NOT NULL, "
            + COLUMN_TIME + " TEXT NOT NULL, "
            + COLUMN_PLACE + " TEXT NOT NULL, "
            + COLUMN_AUTO_DELETE + " INTEGER DEFAULT 0"
            + ")";

    public AppointmentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APPOINTMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }

    // Insert a new appointment
    public long insertAppointment(Appointment appointment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, appointment.getTitle());
        values.put(COLUMN_DOCTOR, appointment.getDoctor());
        values.put(COLUMN_DATE, appointment.getDate());
        values.put(COLUMN_TIME, appointment.getTime());
        values.put(COLUMN_PLACE, appointment.getPlace());
        values.put(COLUMN_AUTO_DELETE, appointment.isAutoDelete() ? 1 : 0);

        long id = db.insert(TABLE_APPOINTMENTS, null, values);
        db.close();
        return id;
    }

    // Get all appointments
    @SuppressLint("Range")
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointmentList = new ArrayList<>();

        // Check if any appointments need to be auto-deleted
        checkAutoDeleteAppointments();

        String selectQuery = "SELECT * FROM " + TABLE_APPOINTMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Appointment appointment = new Appointment();
                appointment.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                appointment.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                appointment.setDoctor(cursor.getString(cursor.getColumnIndex(COLUMN_DOCTOR)));
                appointment.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                appointment.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                appointment.setPlace(cursor.getString(cursor.getColumnIndex(COLUMN_PLACE)));
                appointment.setAutoDelete(cursor.getInt(cursor.getColumnIndex(COLUMN_AUTO_DELETE)) == 1);

                appointmentList.add(appointment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return appointmentList;
    }

    // Get appointment by ID
    @SuppressLint("Range")
    public Appointment getAppointment(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPOINTMENTS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Appointment appointment = null;
        if (cursor != null && cursor.moveToFirst()) {
            appointment = new Appointment();
            appointment.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            appointment.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            appointment.setDoctor(cursor.getString(cursor.getColumnIndex(COLUMN_DOCTOR)));
            appointment.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
            appointment.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
            appointment.setPlace(cursor.getString(cursor.getColumnIndex(COLUMN_PLACE)));
            appointment.setAutoDelete(cursor.getInt(cursor.getColumnIndex(COLUMN_AUTO_DELETE)) == 1);
            cursor.close();
        }
        db.close();
        return appointment;
    }

    // Update appointment
    public int updateAppointment(Appointment appointment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, appointment.getTitle());
        values.put(COLUMN_DOCTOR, appointment.getDoctor());
        values.put(COLUMN_DATE, appointment.getDate());
        values.put(COLUMN_TIME, appointment.getTime());
        values.put(COLUMN_PLACE, appointment.getPlace());
        values.put(COLUMN_AUTO_DELETE, appointment.isAutoDelete() ? 1 : 0);

        int rowsAffected = db.update(TABLE_APPOINTMENTS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(appointment.getId())});
        db.close();
        return rowsAffected;
    }

    // Delete appointment
    public void deleteAppointment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APPOINTMENTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Search appointments
    @SuppressLint("Range")
    public List<Appointment> searchAppointments(String query) {
        List<Appointment> appointmentList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_APPOINTMENTS +
                " WHERE " + COLUMN_TITLE + " LIKE '%" + query + "%'" +
                " OR " + COLUMN_DOCTOR + " LIKE '%" + query + "%'" +
                " OR " + COLUMN_PLACE + " LIKE '%" + query + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Appointment appointment = new Appointment();
                appointment.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                appointment.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                appointment.setDoctor(cursor.getString(cursor.getColumnIndex(COLUMN_DOCTOR)));
                appointment.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                appointment.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                appointment.setPlace(cursor.getString(cursor.getColumnIndex(COLUMN_PLACE)));
                appointment.setAutoDelete(cursor.getInt(cursor.getColumnIndex(COLUMN_AUTO_DELETE)) == 1);

                appointmentList.add(appointment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return appointmentList;
    }

    // Check and auto-delete appointments that have passed
    public void checkAutoDeleteAppointments() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_APPOINTMENTS + " WHERE " + COLUMN_AUTO_DELETE + " = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();

        if (cursor.moveToFirst()) {
            do {
                try {
                    @SuppressLint("Range") String appointmentDate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                    @SuppressLint("Range") String appointmentTime = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
                    @SuppressLint("Range") int appointmentId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));

                    Date date = dateFormat.parse(appointmentDate);
                    Date time = timeFormat.parse(appointmentTime);

                    // Combine date and time
                    Calendar appointmentCal = Calendar.getInstance();
                    appointmentCal.setTime(date);

                    Calendar timeCal = Calendar.getInstance();
                    timeCal.setTime(time);

                    appointmentCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                    appointmentCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));

                    // Check if the appointment time has passed
                    if (appointmentCal.getTime().before(currentDate)) {
                        // Delete the appointment
                        db.delete(TABLE_APPOINTMENTS, COLUMN_ID + " = ?",
                                new String[]{String.valueOf(appointmentId)});
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }
}