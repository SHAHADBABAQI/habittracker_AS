package com.example.habtrac;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HabTrac.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_HABITS = "habits";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";

    public static final String TABLE_HABIT_ENTRIES = "habit_entries";
    public static final String COLUMN_HABIT_ID = "habit_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COMPLETED = "completed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HABITS_TABLE = "CREATE TABLE " + TABLE_HABITS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT);";

        String CREATE_HABIT_ENTRIES_TABLE = "CREATE TABLE " + TABLE_HABIT_ENTRIES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_HABIT_ID + " INTEGER, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_COMPLETED + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + COLUMN_HABIT_ID + ") REFERENCES " + TABLE_HABITS + "(" + COLUMN_ID + "));";

        db.execSQL(CREATE_HABITS_TABLE);
        db.execSQL(CREATE_HABIT_ENTRIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DATABASE_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABIT_ENTRIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
            onCreate(db);
        }
    }

    public long insertHabit(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DESCRIPTION, description);
        return db.insert(TABLE_HABITS, null, values);
    }

    public void markHabitAsCompleted(int habitId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HABIT_ID, habitId);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_COMPLETED, 1);  // Mark as completed
        db.insert(TABLE_HABIT_ENTRIES, null, values);
    }

    // Query to get only incomplete habits for the main view
    public Cursor getIncompleteHabits() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_HABITS + " WHERE " +
                COLUMN_ID + " NOT IN (SELECT " + COLUMN_HABIT_ID +
                " FROM " + TABLE_HABIT_ENTRIES + " WHERE " + COLUMN_COMPLETED + " = 1)";
        return db.rawQuery(query, null);
    }

    public Cursor getCompletedHabits() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT h." + COLUMN_ID + ", h." + COLUMN_NAME + ", h." + COLUMN_DESCRIPTION +
                " FROM " + TABLE_HABITS + " h INNER JOIN " + TABLE_HABIT_ENTRIES + " e " +
                "ON h." + COLUMN_ID + " = e." + COLUMN_HABIT_ID + " WHERE e." + COLUMN_COMPLETED + " = 1";
        return db.rawQuery(query, null);
    }

    public void deleteHabit(int habitId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HABIT_ENTRIES, COLUMN_HABIT_ID + " = ?", new String[]{String.valueOf(habitId)});
        db.delete(TABLE_HABITS, COLUMN_ID + " = ?", new String[]{String.valueOf(habitId)});
    }
}
