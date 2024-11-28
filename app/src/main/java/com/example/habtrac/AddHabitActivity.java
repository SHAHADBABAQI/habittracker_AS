package com.example.habtrac;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddHabitActivity extends AppCompatActivity {

    private EditText edtHabitName, edtDuration;
    private Button btnSetReminder, btnSaveHabit;
    private int reminderHour = -1, reminderMinute = -1;  // Store reminder time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        edtHabitName = findViewById(R.id.edtHabitName);
        edtDuration = findViewById(R.id.edtDuration);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        btnSaveHabit = findViewById(R.id.btnSaveHabit);

        // Set a reminder time when user clicks the "Set Reminder" button
        btnSetReminder.setOnClickListener(v -> showTimePickerDialog());

        // Save the habit to the database when the user clicks "Save Habit"
        btnSaveHabit.setOnClickListener(v -> saveHabit());
    }

    private void showTimePickerDialog() {
        // Use Calendar to set the current time for the dialog
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Show TimePickerDialog to allow user to select time for reminder
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                        reminderHour = hourOfDay;
                        reminderMinute = minuteOfHour;
                        btnSetReminder.setText("Reminder set for " + reminderHour + ":" +
                                (reminderMinute < 10 ? "0" + reminderMinute : reminderMinute));
                    }
                },
                hour,
                minute,
                true);

        timePickerDialog.show();
    }

    private void saveHabit() {
        String habitName = edtHabitName.getText().toString().trim();
        String duration = edtDuration.getText().toString().trim();

        // Validate user input
        if (habitName.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save habit to database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long habitId = dbHelper.insertHabit(habitName, duration);

        if (habitId != -1) {
            // If the habit is saved successfully, set the reminder (if time is selected)
            if (reminderHour != -1 && reminderMinute != -1) {
                setReminder(reminderHour, reminderMinute);
            }

            Toast.makeText(this, "Habit Added!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and return to MainActivity
        } else {
            Toast.makeText(this, "Error adding habit", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setReminder(int hour, int minute) {
        // Create a Calendar object to store the reminder time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Get the AlarmManager system service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            // Create an Intent to broadcast when the alarm goes off
            Intent intent = new Intent(this, ReminderReceiver.class);  // The receiver that will show the notification

            // Create a PendingIntent for the broadcast receiver
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set the alarm to trigger at the specified time
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            // Show a toast message indicating the reminder was set
            Toast.makeText(this, "Reminder set for " + hour + ":" + (minute < 10 ? "0" + minute : minute), Toast.LENGTH_SHORT).show();
        } else {
            // In case AlarmManager is not available
            Log.e("AddHabitActivity", "AlarmManager is null. Could not set reminder.");
        }
    }
}
