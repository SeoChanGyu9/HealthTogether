package com.example.firebase_test.alarm.models

data class Time(
    var hour: Int = 0,
    var minute: Int = 0,
    var am_pm: String = "",
    var month: String = "",
    var day: String = ""
) {

    companion object {
        fun fromString(string: String) =
            Time(

            )
    }
}
