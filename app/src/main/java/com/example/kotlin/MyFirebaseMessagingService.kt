package com.example.kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notification_channel"
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
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_IMMUTABLE)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.logo)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0/*ID of notification*/, notificationBuilder.build())


        // cach 2
//        val builder: NotificationCompat.Builder =
//            NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.logo)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(true)
//        val manager = NotificationManagerCompat.from(this)
//        manager.notify(101, builder.build())

        // cach 3
//        Log.d("generateNotification", "generateNotification: $title\n$message")
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//        Log.d("1","1")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
//            notificationManager.createNotificationChannel(channel)
//        }
//        Log.d("2","2")
//
//        // Create custom notification layout
//        val contentView = RemoteViews(packageName, R.layout.notification)
//        contentView.setTextViewText(R.id.txtTitle, title)
//        contentView.setTextViewText(R.id.txtMessage, message)
//
//        Log.d("3","3")
//
//        // Create intent for when notification is clicked
//        val intent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        Log.d("4","4")
//
//        // Build notification
//        val builder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.logo)
//            .setContent(contentView)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        Log.d("5","5")
//
//        // Show notification
//        notificationManager.notify(0, builder.build())
//
//        Log.d("6","6")
    }

}