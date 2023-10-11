package com.example.firebase_test.alarm

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.firebase_test.R
import com.example.firebase_test.alarm.models.Time
import java.text.SimpleDateFormat
import java.util.Calendar

class AlarmActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE1 = 1000
        const val REQUEST_CODE2 = 1001
        const val RECEIVCER_REQUEST_CODE = 101
        private const val PERMISSION_CODE = 102
    }

    private lateinit var arrayAdapter: AdapterActivity
    private lateinit var tpBtn: Button
    private lateinit var listView: ListView
    private lateinit var removeBtn: Button
    private var hour = 0
    private var minute = 0
    private lateinit var month: String
    private lateinit var day: String
    private lateinit var am_pm: String
    private var selectedPosition: Int? = null
    private lateinit var timeListSaver: TimeListSaver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_main)

        timeListSaver = TimeListSaver(this)

        requestNotiPer()

        arrayAdapter = AdapterActivity()
        listView = findViewById(R.id.list_view)
        listView.adapter = arrayAdapter
        arrayAdapter.setItemList(timeListSaver.getTimeList())

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedPosition = position
        }

        val handler = Handler(Looper.getMainLooper()) {
            val cal = Calendar.getInstance()
            val mFormat = SimpleDateFormat("HH:mm:ss")
            val strTime = mFormat.format(cal.time)
            val textView = findViewById<TextView>(R.id.current)
            textView.textSize = 30f
            textView.text = strTime
            true
        }

        handler.sendEmptyMessage(0)

        val runnable = Runnable {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                handler.sendEmptyMessage(0)
            }
        }

        val thread = Thread(runnable)
        thread.start()

        tpBtn = findViewById(R.id.addBtn)
        tpBtn.setOnClickListener {
            val tpIntent = Intent(this@AlarmActivity, TimePickerActivity::class.java)
            startActivityForResult(tpIntent, REQUEST_CODE1)
        }

        removeBtn = findViewById(R.id.removeBtn)
        removeBtn.setOnClickListener {
            selectedPosition?.let { position ->
                arrayAdapter.removeItem(position)
                arrayAdapter.notifyDataSetChanged()
                timeListSaver.saveTimeList(arrayAdapter.getItemList())
                selectedPosition = null
            }
        }

        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val time = intent?.getStringExtra("time")
        if (time != null) {
            startActivity(Intent(this, AlarmResultActivity::class.java).apply {
                putExtra("time", time)
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_CANCELED) {
            return
        }

        if (resultCode == RESULT_OK && data != null) {
            hour = data.getIntExtra("hour", 1)
            minute = data.getIntExtra("minute", 2)
            am_pm = data.getStringExtra("am_pm").orEmpty()
            month = data.getStringExtra("month").orEmpty()
            day = data.getStringExtra("day").orEmpty()

            when (requestCode) {
                REQUEST_CODE1, REQUEST_CODE2 -> {
                    arrayAdapter.addItem(hour, minute, am_pm, month, day)
                    val hour = if (am_pm == "오후") hour + 12 else hour
                    setAlarm(
                        Time(
                            month = month,
                            day = day,
                            hour = hour,
                            minute = minute,
                            am_pm = am_pm
                        )
                    )
                    timeListSaver.saveTimeList(arrayAdapter.getItemList()) //
                    // 로컬저장
                    arrayAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun requestNotiPer() {
        ActivityCompat.requestPermissions(
            this,
            listOf(POST_NOTIFICATIONS).toTypedArray(),
            PERMISSION_CODE
        )
    }

    /**
     * 알람 설정
     */
    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(time: Time) {
        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = Intent(applicationContext, CustomAlarmReceiver::class.java).let {
            it.putExtra("code", RECEIVCER_REQUEST_CODE)
            it.putExtra("time", "${time.hour}시 ${time.minute}분")
            PendingIntent.getBroadcast(
                applicationContext, RECEIVCER_REQUEST_CODE, it,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        Log.d("time", "set: ${time.hour}시 ${time.minute}분")
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.MONTH, time.month.toInt() -1 )
            set(Calendar.DAY_OF_MONTH, time.day.toInt())
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        alarmManager.setAlarmClock(
            AlarmClockInfo(calendar.timeInMillis, pendingIntent),
            pendingIntent
        )
    }
}

