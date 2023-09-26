package com.example.firebase_test

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    // 메세지가 수신되면 호출
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // 서버에서 직접 보냈을 때
        if(remoteMessage.notification != null){
            sendNotification(remoteMessage.notification?.title,
                remoteMessage.notification?.body!!)
        }

        // 다른 기기에서 서버로 보냈을 때
        else if(remoteMessage.data.isNotEmpty()){
            val title = remoteMessage.data["title"]!!
            val userId = remoteMessage.data["userId"]!!
            val message = remoteMessage.data["message"]!!

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                sendMessageNotification(title,userId,message)
            }
            else{
                sendNotification(remoteMessage.notification?.title,
                    remoteMessage.notification?.body!!)
            }
        }
    }

    // Firebase Cloud Messaging Server 가 대기중인 메세지를 삭제 시 호출
    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    // 메세지가 서버로 전송 성공 했을때 호출
    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
        Log.d("로그","메세지가 서버로 전송 성공")
    }

    // 메세지가 서버로 전송 실패 했을때 호출
    override fun onSendError(p0: String, p1: Exception) {
        super.onSendError(p0, p1)
        Log.d("로그","메세지가 서버로 전송 실패")
    }

    // 새로운 토큰이 생성 될 때 호출
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
        Log.d("로그","새로운 토큰 생성")
    }

    // 서버에서 직접 보냈을 때
    private fun sendNotification(title: String?, body: String){
        Log.d("로그","서버에서 직접 메세지를 보냄")
        val intent = Intent(this, MainActivity2::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 액티비티 중복 생성 방지
        val pendingIntent = PendingIntent.getActivity(this, 0 , intent,
            PendingIntent.FLAG_ONE_SHOT) // 일회성

        val channelId = "channel" // 채널 아이디
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 소리
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title) // 제목
            .setContentText(body) // 내용
            .setSmallIcon(R.drawable.ic_launcher_background) // 아이콘
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 예외처리
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 , notificationBuilder.build()) // 알림 생성
    }


    // 다른 기기에서 서버로 보냈을 때
    @RequiresApi(Build.VERSION_CODES.P)
    private fun sendMessageNotification(title: String,userId: String, body: String){
        Log.d("로그","다른기기에서 서버로 보냄")
        val intent = Intent(this, MainActivity2::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 액티비티 중복 생성 방지
        //val pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT) // 일회성
        val pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // messageStyle 로
        val user: androidx.core.app.Person = Person.Builder()
            .setName(userId)
            .setIcon(IconCompat.createWithResource(this,R.drawable.ic_launcher_foreground))
            .build()

        val message = NotificationCompat.MessagingStyle.Message(
            body,
            System.currentTimeMillis(),
            user
        )
        val messageStyle = NotificationCompat.MessagingStyle(user)
            .addMessage(message)


        val channelId = "channel" // 채널 아이디
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 소리
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title) // 제목
            .setContentText(body) // 내용
            .setStyle(messageStyle)
            .setSmallIcon(R.drawable.ic_launcher_background) // 아이콘
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 예외처리
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "알림 메세지",
                NotificationManager.IMPORTANCE_LOW) // 소리없앰
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 , notificationBuilder.build()) // 알림 생성
    }

    // 받은 토큰을 서버로 전송
    private fun sendRegistrationToServer(token: String){

    }
}