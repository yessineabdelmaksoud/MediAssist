package services.Appointement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applicationproject.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private EditText titleEditText, doctorEditText, placeEditText;
    private TextView dateTextView, timeTextView;
    private LinearLayout datePickerLayout, timePickerLayout;
    private CheckBox autoDeleteCheckBox;
    private Button saveButton;
    private ImageButton backButton;

    private AppointmentDbHelper dbHelper;
    private Calendar selectedDate;
    private Calendar selectedTime;
    private int appointmentId = -1;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        // Initialize date and time formats
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Initialize views
        titleEditText = findViewById(R.id.titleEditTextAppointment);
        doctorEditText = findViewById(R.id.doctorEditTextAppointment);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.heureTextView);
        datePickerLayout = findViewById(R.id.datePickerLayoutAppointment);
        timePickerLayout = findViewById(R.id.HeurePickerLayoutAppointment);
        placeEditText = findViewById(R.id.LieuEditTextAppointment);
        autoDeleteCheckBox = findViewById(R.id.autoDeleteCheckBox);
        saveButton = findViewById(R.id.saveButtonAppointment);
        backButton = findViewById(R.id.backButtonappointment);

        // Initialize database helper
        dbHelper = new AppointmentDbHelper(this);

        // Initialize calendars
        selectedDate = Calendar.getInstance();
        selectedTime = Calendar.getInstance();

        // Check if we're editing an existing appointment
        if (getIntent().hasExtra("appointment_id")) {
            appointmentId = getIntent().getIntExtra("appointment_id", -1);
            loadAppointmentData(appointmentId);
        }

        // Setup date picker
        datePickerLayout.setOnClickListener(v -> showDatePicker());

        // Setup time picker
        timePickerLayout.setOnClickListener(v -> showTimePicker());

        // Setup save button
        saveButton.setOnClickListener(v -> saveAppointment());

        // Setup back button
        backButton.setOnClickListener(v -> finish());
    }

    private void loadAppointmentData(int appointmentId) {
        Appointment appointment = dbHelper.getAppointment(appointmentId);
        if (appointment != null) {
            titleEditText.setText(appointment.getTitle());
            doctorEditText.setText(appointment.getDoctor());
            placeEditText.setText(appointment.getPlace());
            dateTextView.setText(appointment.getDate());
            timeTextView.setText(appointment.getTime());
            autoDeleteCheckBox.setChecked(appointment.isAutoDelete());

            try {
                // Parse date and time
                selectedDate.setTime(dateFormat.parse(appointment.getDate()));
                selectedTime.setTime(timeFormat.parse(appointment.getTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dateTextView.setText(dateFormat.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute);
                    timeTextView.setText(timeFormat.format(selectedTime.getTime()));
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void saveAppointment() {
        // Validate input fields
        String title = titleEditText.getText().toString().trim();
        String doctor = doctorEditText.getText().toString().trim();
        String place = placeEditText.getText().toString().trim();
        String date = dateTextView.getText().toString().trim();
        String time = timeTextView.getText().toString().trim();
        boolean autoDelete = autoDeleteCheckBox.isChecked();

        if (title.isEmpty() || doctor.isEmpty() || place.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create appointment object
        Appointment appointment = new Appointment(title, doctor, date, time, place, autoDelete);

        // If editing, set the appointment ID
        if (appointmentId != -1) {
            appointment.setId(appointmentId);
            dbHelper.updateAppointment(appointment);
            Toast.makeText(this, "Appointment mis à jour", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.insertAppointment(appointment);
            Toast.makeText(this, "Appointment ajouté", Toast.LENGTH_SHORT).show();
        }

        // Return to appointments list
        finish();
    }
}
