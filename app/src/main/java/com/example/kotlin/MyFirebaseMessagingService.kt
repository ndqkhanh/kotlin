package com.example.kotlin

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelID = "notification_channel"
const val channelName = "com.example.kotlin"

class MyFirebaseMessagingService :FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("MyFirebaseMessagingService", "onNewToken: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "${remoteMessage.notification?.title}\n${remoteMessage.notification?.body}")
        if (remoteMessage.notification != null){
            val title = remoteMessage.notification!!.title
            val message = remoteMessage.notification!!.body
            generateNotification(title!!, message!!)
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun generateNotification(title: String, message: String) {
        Log.d("generateNotification", "generateNotification: $title\n$message")
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        Log.d("1","1")

        val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        Log.d("2","2")

        // Create custom notification layout
        val contentView = RemoteViews(packageName, R.layout.notification)
        contentView.setTextViewText(R.id.txtTitle, title)
        contentView.setTextViewText(R.id.txtMessage, message)

        Log.d("3","3")

        // Create intent for when notification is clicked
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        Log.d("4","4")

        // Build notification
        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.logo)
            .setContent(contentView)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        Log.d("5","5")

        // Show notification
        notificationManager.notify(0, builder.build())

        Log.d("6","6")
    }

}