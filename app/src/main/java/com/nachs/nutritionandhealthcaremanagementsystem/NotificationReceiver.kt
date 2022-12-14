package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = intent.getIntExtra("notificationId", 0)
        val channelId = "channelId"
        val channelName = "channelName"
        val importance = NotificationManager.IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra("title"))
            .setContentText(intent.getStringExtra("content"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(notificationId, builder.build())
    }

    fun getPendingIntent(
        context: Context,
        notificationId: Int,
        title: String,
        content: String
    ): android.app.PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("content", content)
        return android.app.PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}