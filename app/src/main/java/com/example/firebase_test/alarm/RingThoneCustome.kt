package com.example.firebase_test.alarm

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Vibrator

object RingToneCustom {
    private val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    private var ringtone: Ringtone? = null
    fun getRingTome(context: Context): Ringtone? {
        if (ringtone == null) {
            ringtone = RingtoneManager.getRingtone(context, uri)
        }
        return ringtone
    }

    private var vibrator: Vibrator? = null

    fun getVibrator(context: Context): Vibrator? {
        if (vibrator == null) {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        return vibrator
    }
}