package com.example.firebase_test.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import com.example.firebase_test.R
import com.example.firebase_test.alarm.AlarmActivity.Companion.RECEIVCER_REQUEST_CODE

class CustomAlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "ALARM_CUSTOM"
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.getIntExtra("code", 0) == RECEIVCER_REQUEST_CODE) {

            val manager = createNotificationChannel(context)

            val time = intent.getStringExtra("time")
            Log.d("time", "Receiver: $time")

            val purposeIntent = Intent(context, AlarmActivity::class.java).apply {
                putExtra("time", time)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context, RECEIVCER_REQUEST_CODE, purposeIntent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            RingToneCustom.getRingTome(context)?.play()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                RingToneCustom.getVibrator(context)?.vibrate(
                    VibrationEffect.createOneShot(
                        2000,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                RingToneCustom.getVibrator(context)?.vibrate(2000)
            }

            // Notification
            val builder01: Builder = Builder(context, CHANNEL_ID).apply {
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setContentTitle("알람 시작 및 종료 시간입니다")
                setContentText(time)
                priority = NotificationCompat.PRIORITY_DEFAULT
                setContentIntent(pendingIntent)
                setAutoCancel(true)
            }

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            manager?.notify(5, builder01.build())
        }
    }

    private fun createNotificationChannel(context: Context?): NotificationManager? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MyAlarm"
            val descriptionText = "알람 울림"
            val channel = NotificationChannel(
                CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = descriptionText
            }

            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            return notificationManager
        }
        return null
    }
}