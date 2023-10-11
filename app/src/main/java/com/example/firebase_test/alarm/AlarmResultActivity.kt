package com.example.firebase_test.alarm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_test.R

class AlarmResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_result)
        val text = intent.getStringExtra("time")
        Log.d("time", "result: $text")
        findViewById<TextView>(R.id.tv).text = text
        findViewById<Button>(R.id.btn).setOnClickListener {
            startActivity(Intent(this, AlarmActivity::class.java))
            finish()
            RingToneCustom.getRingTome(applicationContext)?.stop()
            RingToneCustom.getVibrator(applicationContext)?.cancel()
        }
    }
}