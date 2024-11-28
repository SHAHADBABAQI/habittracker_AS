package com.example.habtrac;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private HabitAdapter habitAdapter;
    private List<Habit> habitList;
    private DatabaseHelper dbHelper;
    private Button btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the button
        btnHistory = findViewById(R.id.btnHistory);

        // Set an onClick listener to open HistoryActivity when clicked
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        dbHelper = new DatabaseHelper(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_habits);
        Button addHabitButton = findViewById(R.id.btn_add_habit);

        habitList = new ArrayList<>();

        // Initialize the HabitAdapter once, passing the listener as well
        habitAdapter = new HabitAdapter(this, habitList, new HabitAdapter.OnHabitCompleteListener() {
            @Override
            public void onHabitComplete(Habit habit) {
                // Insert a new entry for today's completion
                String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                dbHelper.markHabitAsCompleted(habit.getId(), todayDate);

                // Remove the completed habit from the list
                habitList.remove(habit);

                // Notify the adapter to refresh the RecyclerView
                habitAdapter.notifyDataSetChanged();
            }


            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onHabitDelete(Habit habit) {
                // Delete the habit from the database
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete(DatabaseHelper.TABLE_HABITS,
                        DatabaseHelper.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(habit.getId())});

                // Remove the habit from the list and update RecyclerView
                habitList.remove(habit);
                habitAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);

        // Load habits from the database
        loadHabits();

        // Set up listener for the "Add Habit" button
        addHabitButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddHabitActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHabits();  // Refresh the RecyclerView when MainActivity resumes
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadHabits() {
        habitList.clear();  // Clear existing list before reloading habits
        Cursor cursor = dbHelper.getIncompleteHabits();  // Query the database for incomplete habits

        if (cursor != null) {
            // Get column indices
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
            int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
            int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION);
            int completedIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_COMPLETED); // Added column for completion

            // Check if all column indexes are valid (>= 0)
            if (idIndex >= 0 && nameIndex >= 0 && descriptionIndex >= 0 && completedIndex >= 0) {
                // Loop through the cursor and retrieve data
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(idIndex);  // Get the ID
                    String name = cursor.getString(nameIndex);  // Get the name
                    String description = cursor.getString(descriptionIndex);  // Get the description
                    boolean isCompleted = cursor.getInt(completedIndex) == 1;

                    // Add the habit to the list
                    habitList.add(new Habit(id, name, description, isCompleted));
                }
                cursor.close();  // Close the cursor when done
            } else {
                // If column indexes are invalid, log an error
                Log.e("Database", "One or more columns are missing in the cursor.");
            }

            // Post the update to the main thread to avoid errors during layout
            new Handler(Looper.getMainLooper()).post(() -> {
                // Notify the adapter that the data set has changed
                habitAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
            });
        }
    }




}
