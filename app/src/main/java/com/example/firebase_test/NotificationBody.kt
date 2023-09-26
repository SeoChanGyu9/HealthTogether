package com.example.firebase_test

//알림메시지
data class NotificationBody(    //to - token , data - 아래데이터
    val to: String,
    val data: NotificationData
) {
    data class NotificationData(    // 알림메시지 데이터들
        val title: String,
        val userId : String,
        val message: String
    )
}

class Constants {

    companion object {
        // FCM URL
        const val FCM_URL = "https://fcm.googleapis.com"
    }
}

