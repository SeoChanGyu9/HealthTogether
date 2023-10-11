package com.example.firebase_test.alarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_test.R
import java.util.*

@Suppress("DEPRECATION")
class TimePickerActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var okBtn: Button
    private lateinit var cancelBtn: Button
    private var hour = 0
    private var minute = 0
    private lateinit var am_pm: String
    private lateinit var currentTime: Date
    private lateinit var stMonth: String
    private lateinit var stDay: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_picker)

        timePicker = findViewById(R.id.time_picker)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        currentTime = calendar.time

        /**
         * 날짜 선택 부분
         */
        val datePicker = findViewById<DatePicker>(R.id.date_picker)
        stMonth = (calendar.get(Calendar.MONTH) + 1).toString()
        stDay = calendar.get(Calendar.DAY_OF_MONTH).toString()
        datePicker.setOnDateChangedListener { datePicker, i, month, day ->
            stMonth = (month + 1).toString()
            stDay = day.toString()
        }

        okBtn = findViewById(R.id.okBtn)
        okBtn.setOnClickListener {

            // 다양한 Android API 버전에 대해 시간 값을 다르게 설정합니다. 이 경우에는 Android API 23을 의미
            hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.hour
            } else {
                timePicker.currentHour
            }

            minute = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.minute
            } else {
                timePicker.currentMinute
            }

            am_pm = AM_PM(hour)
            hour = timeSet(hour)

            val sendIntent = Intent(this@TimePickerActivity, AlarmActivity::class.java)
            sendIntent.putExtra("hour", hour)
            sendIntent.putExtra("minute", minute)
            sendIntent.putExtra("am_pm", am_pm)
            sendIntent.putExtra("month", stMonth)
            sendIntent.putExtra("day", stDay)
            setResult(RESULT_OK, sendIntent)

            finish()
        }

        // 취소 버튼을 클릭하면 TimePickerActivity를 닫음
        cancelBtn = findViewById(R.id.cancleBtn)
        cancelBtn.setOnClickListener { finish() }

    }

    // 12시간 형식으로 변환
    private fun timeSet(hour: Int): Int = if (hour > 12) hour - 12 else hour

    // 오전(AM)인지 오후(PM)인지 결정
    private fun AM_PM(hour: Int): String = if (hour >= 12) "오후" else "오전"
}
