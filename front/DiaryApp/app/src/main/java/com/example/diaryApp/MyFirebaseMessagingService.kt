package com.example.diaryApp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "FCM 토큰: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "메시지 받음: ${remoteMessage.notification?.body}")

        if (remoteMessage.data.isNotEmpty()) {
            // `data` 필드에서 값 읽기
            val title = remoteMessage.notification?.title
            val body =remoteMessage.notification?.body
            val titleId = remoteMessage.data["titleId"]           // `titleId` 읽기
            val name = remoteMessage.data["name"]                 // `name` 읽기

            // 읽어온 데이터로 알림 생성
            sendNotification(title, body, titleId, name)
        }
    }

    private fun sendNotification(title: String?, body: String?, titleId: String?, childName: String?) {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"

        // 알림 채널 생성 (Android 8.0 이상)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val targetPath = when (title) {
            "그림 퀴즈" -> "notification" // 동적 경로
            "그림 일기" -> "diary/$titleId/$childName"
            "쿠폰" -> "shop"
            else -> "main" // 기본 경로
        }
        val notificationMessage = when (title) {
            "그림 퀴즈" -> "$childName - 그림 퀴즈를 요청 했어요"
            "그림 일기" -> "$childName - 그림 일기를 업로드 했어요"
            "쿠폰" -> "$childName - 쿠폰을 구매 했어요"
            else -> "$childName - 새로운 활동을 했어요"
        }
        Log.d("alarm","targetPath: ${targetPath}")
        // 알림을 클릭했을 때 열릴 액티비티 설정
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigation_target",targetPath)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w("FCM", "알림 권한이 없습니다.")
            return
        }

        // 알림 빌더 설정
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.daily_logo)
            .setContentTitle(title)
            .setContentText(notificationMessage)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 알림 표시
        with(NotificationManagerCompat.from(this)) {
            notify(0, notificationBuilder.build())
        }
    }
}