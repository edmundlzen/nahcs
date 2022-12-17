package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

public suspend fun refreshAppointmentNotifications(context: Context) =
    withContext(Dispatchers.Default) {
        val db = Firebase.firestore
        val userAuth = Firebase.auth.currentUser
        val appointmentsRef =
            db.collection("appointments")
        val isNutritionist =
            context.getSharedPreferences(
                "prefs",
                MODE_PRIVATE
            )
                .getBoolean("isNutritionist", false)
        val existingAppointmentNotificationIds =
            context.getSharedPreferences("data", 0).getStringSet(
                "appointmentNotificationIds",
                mutableSetOf()
            )!!
        val appointments =
            appointmentsRef.whereEqualTo(
                if (isNutritionist) "nutritionist" else "user",
                userAuth?.uid
            )
                .get()
                .await()

        for (document in appointments) {
            val userName = db.collection("users")
                .document(document[if (isNutritionist) "user" else "nutritionist"].toString())
                .get()
                .await()
                .getString("name")
            val calendar = Calendar.getInstance()
            calendar.time = document.getDate("date")!!
            calendar.set(
                Calendar.HOUR,
                document.getString("time")!!.split(":")[0].toInt()
            )
            calendar.set(
                Calendar.AM_PM,
                if (document.getString("time")!!
                        .split(" ")[1] == "AM"
                ) Calendar.AM else Calendar.PM
            )
            val selectedDateTime = calendar.time
            if (selectedDateTime.before(Date())) {
                continue
            }

            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent("com.nachs.nutritionandhealthcaremanagementsystem.NOTIFICATION")
            intent.putExtra("NOTIFICATION_TITLE", "Appointment reminder")
            intent.putExtra(
                "NOTIFICATION_TEXT", "You have an appointment with ${userName} at ${
                    SimpleDateFormat("h:mm a").format(
                        selectedDateTime
                    )
                } ${SimpleDateFormat("EEEE, d MMMM").format(selectedDateTime)}"
            )
            intent.putExtra("NOTIFICATION_ID", document.id.hashCode())
            intent.component = ComponentName(
                "com.nachs.nutritionandhealthcaremanagementsystem",
                "com.nachs.nutritionandhealthcaremanagementsystem.NotificationReceiver"
            )
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                document.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 30 minutes before the appointment
            val notificationTime = calendar.timeInMillis - 1000 * 60 * 30

            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime, pendingIntent
                )
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime, pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime, pendingIntent
                )
            }
            Log.d("Notification", "Set notification for ${document.id}")
            if (!existingAppointmentNotificationIds.contains(document.id)) {
                existingAppointmentNotificationIds.add(document.id)
                context.getSharedPreferences("data", 0).edit().putStringSet(
                    "appointmentNotificationIds",
                    existingAppointmentNotificationIds
                ).apply()
            }
        }

        // Remove notifications for appointments that are no longer in the database
        for (existingAppointmentNotificationId in existingAppointmentNotificationIds) {
            if ((appointments.map { it.id }).contains(existingAppointmentNotificationId)) {
                continue
            }

            // Remove the existing notification
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent("com.nachs.nutritionandhealthcaremanagementsystem.NOTIFICATION")
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                existingAppointmentNotificationId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
        }
        return@withContext
    }