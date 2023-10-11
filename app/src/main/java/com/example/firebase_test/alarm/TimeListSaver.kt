package com.example.firebase_test.alarm

import android.content.Context
import android.util.Log
import com.example.firebase_test.alarm.models.Time
import com.google.gson.Gson


/**
 * 로컬 저장 클래스
 */

class TimeListSaver(context: Context) {

    private val sharedPreference =
        context.getSharedPreferences("cust_alarm", Context.MODE_PRIVATE)

    fun saveTimeList(list: List<Time>) {
        with(sharedPreference.edit()) {
            putStringSet("timeSet", list.map { Gson().toJson(it) }.toSet())
            apply()
        }
    }

    fun getTimeList(): List<Time> {
        val list =
            sharedPreference.getStringSet("timeSet", emptySet()).orEmpty()
                .map { Gson().fromJson(it, Time::class.java) }
        Log.d("GsonResult", "$list")
        return list
    }
}