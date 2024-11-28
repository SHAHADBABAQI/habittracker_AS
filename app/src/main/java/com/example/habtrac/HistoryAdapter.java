package com.example.habtrac;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<Habit> {

    public HistoryAdapter(Context context, ArrayList<Habit> habits) {
        super(context, 0, habits);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_habit, parent, false);
        }

        Habit habit = getItem(position);

        TextView habitName = convertView.findViewById(R.id.habit_name);
        TextView habitDescription = convertView.findViewById(R.id.habit_description);
        CheckBox checkboxComplete = convertView.findViewById(R.id.checkbox_complete);
        Button btnDelete = convertView.findViewById(R.id.btn_delete);

        habitName.setText(habit.getName());
        habitDescription.setText(habit.getDescription());
        checkboxComplete.setChecked(habit.isCompletedToday());

        btnDelete.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            dbHelper.deleteHabit(habit.getId());
            remove(habit);
            notifyDataSetChanged();
        });

        return convertView;
    }
}
