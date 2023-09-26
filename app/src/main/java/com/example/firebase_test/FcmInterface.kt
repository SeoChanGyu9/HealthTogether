package com.example.firebase_test

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

//레트로핏의 3가지 구성요소 중 Interface 역할을 할 인터페이스를 생성해준다.

interface FcmInterface {
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: NotificationBody
    ) : Response<ResponseBody>
}