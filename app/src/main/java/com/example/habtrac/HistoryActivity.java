package com.example.habtrac;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnBack;
    private HistoryAdapter adapter;
    private ArrayList<Habit> completedHabits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        btnBack = findViewById(R.id.btnBack);
        completedHabits = new ArrayList<>();

        adapter = new HistoryAdapter(this, completedHabits);
        listView.setAdapter(adapter);

        loadCompletedHabits();

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadCompletedHabits() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getCompletedHabits();

        if (cursor != null && cursor.moveToFirst()) {
            completedHabits.clear();
            do {
                // Check if column indices are valid before accessing data
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
                int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION);

                if (idIndex >= 0 && nameIndex >= 0 && descriptionIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    String description = cursor.getString(descriptionIndex);

                    completedHabits.add(new Habit(id, name, description, true));
                } else {
                    // Log a warning if any column is missing
                    Log.e("HistoryActivity", "Column index not found in cursor.");
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.e("HistoryActivity", "No completed habits found.");
        }
        adapter.notifyDataSetChanged();
    }

}
