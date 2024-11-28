package com.example.habtrac;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private Context context;
    private List<Habit> habitList;
    private OnHabitCompleteListener onHabitCompleteListener;

    // Constructor to initialize the context, habit list, and listener
    public HabitAdapter(Context context, List<Habit> habitList, OnHabitCompleteListener onHabitCompleteListener) {
        this.context = context;
        this.habitList = habitList;
        this.onHabitCompleteListener = onHabitCompleteListener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(itemView); // Create and return the ViewHolder
    }

    @Override
    public void onBindViewHolder(HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);

        // Set the text for the habit's name and description
        holder.habitNameTextView.setText(habit.getName());
        holder.habitDescriptionTextView.setText(habit.getDescription());

        // Set the checkbox state based on whether the habit is completed for today
        holder.checkboxComplete.setChecked(habit.isCompletedToday());

        // Set a listener to handle when the checkbox state changes
        holder.checkboxComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            habit.setCompletedToday(isChecked); // Update the habit's completion status

            // Post the update to the main thread to ensure it's not happening during layout computation
            new Handler(Looper.getMainLooper()).post(() -> {
                // Notify that the item at the given position has changed
                notifyItemChanged(position);

                // Notify the listener that the habit's completion status has been updated
                if (onHabitCompleteListener != null) {
                    onHabitCompleteListener.onHabitComplete(habit);
                }
            });
        });

        // Set the delete button click listener
        holder.delButton.setOnClickListener(v -> {
            // Notify the listener to delete the habit
            if (onHabitCompleteListener != null) {
                onHabitCompleteListener.onHabitDelete(habit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size(); // Return the size of the habit list
    }

    // Interface to notify when a habit is completed or deleted
    public interface OnHabitCompleteListener {
        void onHabitComplete(Habit habit);
        void onHabitDelete(Habit habit);
    }

    // ViewHolder class to hold references to each view in the item_habit layout
    public class HabitViewHolder extends RecyclerView.ViewHolder {

        // Declare references to the views
        TextView habitNameTextView;
        TextView habitDescriptionTextView;
        CheckBox checkboxComplete;  // Declare CheckBox for habit completion
        Button delButton;

        // Constructor to initialize the views
        public HabitViewHolder(View itemView) {
            super(itemView);
            // Initialize the views using findViewById
            habitNameTextView = itemView.findViewById(R.id.habit_name);
            habitDescriptionTextView = itemView.findViewById(R.id.habit_description);
            checkboxComplete = itemView.findViewById(R.id.checkbox_complete); // Initialize the CheckBox
            delButton = itemView.findViewById(R.id.btn_delete);

        }
    }
}
