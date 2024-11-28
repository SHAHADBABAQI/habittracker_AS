package com.example.habtrac;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "habit_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create the notification channel (only for Android O and above)
        createNotificationChannel(context);

        // Build the notification
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Habit Reminder")
                .setContentText("It's time to complete your habit!")
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                //.setSmallIcon(R.drawable.ic_reminder)  // Ensure this icon exists
                .setAutoCancel(true)
                .build();

        // Get the NotificationManager and show the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            // Use a unique ID for each notification (e.g., use current time or habit ID)
            int notificationId = (int) System.currentTimeMillis();  // Or use a habit ID if passed in intent
            notificationManager.notify(notificationId, notification);  // Display the notification
        }
    }

    // Create a notification channel (for Android O and above)
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Habit Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
