package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("NOTIFICATION_TITLE") ?: return
        val text = intent.getStringExtra("NOTIFICATION_TEXT") ?: return
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)

        val notificationsEnabled =
            context.getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("notificationsOn", true)
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
        val id = "my_notification_channel"
        val name = "My Notification Channel"
        val description = "My Notification Channel Description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(id, name, importance)
        mChannel.description = description
        mChannel.enableLights(true)
        mChannel.enableVibration(true)
        mNotificationManager.createNotificationChannel(mChannel)

        val builder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setChannelId(id)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }

        notifiedAppointmentNotifications.add(notificationId.toString())
        context.getSharedPreferences("data", MODE_PRIVATE).edit().putStringSet(
            "notifiedAppointmentNotificationIds",
            notifiedAppointmentNotifications
        ).clear().apply()
        Log.d("NotificationReceiver", "Notification has been sent")
    }
}