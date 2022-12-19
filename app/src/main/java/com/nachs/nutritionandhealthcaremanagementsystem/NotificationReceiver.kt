package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("NOTIFICATION_TITLE") ?: return
        val text = intent.getStringExtra("NOTIFICATION_TEXT") ?: return
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)
        val isWaterReminder = intent.getBooleanExtra("IS_WATER_REMINDER", false)
        val isExerciseReminder = intent.getBooleanExtra("IS_EXERCISE_REMINDER", false)

        // Do not send any notifications if the time is not between 7am and 10pm
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (currentHour < 7 || currentHour > 22) return

        if (
            !isWaterReminder && !isExerciseReminder
        ) {
            val notificationsEnabled =
                context.getSharedPreferences("prefs", MODE_PRIVATE)
                    .getBoolean("notificationsOn", true)
            if (!notificationsEnabled) return

            val notifiedAppointmentNotifications =
                context.getSharedPreferences("data", MODE_PRIVATE).getStringSet(
                    "notifiedAppointmentNotificationIds",
                    mutableSetOf()
                )!!

            if (notifiedAppointmentNotifications.contains(notificationId.toString())) {
                Log.d("NotificationReceiver", "Notification already sent")
                return
            }
            val mNotificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val id = "appointment_reminder_channel"
            val name = "Appointment Reminders"
            val description = "Reminders for upcoming appointments"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mNotificationManager.createNotificationChannel(mChannel)
            val resultIntent = Intent(context, Home::class.java)
            val resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(id)
                .setContentIntent(
                    resultPendingIntent
                )

            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }

            notifiedAppointmentNotifications.add(notificationId.toString())
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putStringSet(
                "notifiedAppointmentNotificationIds",
                notifiedAppointmentNotifications
            ).clear().apply()
            Log.d("NotificationReceiver", "Notification has been sent")
        } else if (isWaterReminder) {
            val notificationsEnabled =
                context.getSharedPreferences("prefs", MODE_PRIVATE)
                    .getBoolean("notificationsOn", true)
            if (!notificationsEnabled) return

            val mNotificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val id = "water_reminder_channel"
            val name = "Water Reminders"
            val description = "Reminders to drink water"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mNotificationManager.createNotificationChannel(mChannel)
            val resultIntent = Intent(context, Home::class.java)
            val resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(id)
                .setContentIntent(
                    resultPendingIntent
                )

            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
            Log.d("NotificationReceiver", "Notification has been sent")
        } else {
            val notificationsEnabled =
                context.getSharedPreferences("prefs", MODE_PRIVATE)
                    .getBoolean("exerciseReminderNotificationsOn", true)
            if (!notificationsEnabled) return

            val mNotificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val id = "exercise_reminder_channel"
            val name = "Exercise Reminders"
            val description = "Reminders to exercise"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mNotificationManager.createNotificationChannel(mChannel)
            val resultIntent = Intent(context, Home::class.java)
            val resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(id)
                .setContentIntent(
                    resultPendingIntent
                )

            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
            Log.d("NotificationReceiver", "Notification has been sent")
        }
    }
}