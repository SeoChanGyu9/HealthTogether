package com.example.firebase_test

import com.example.firebase_test.Constants.Companion.FCM_URL
//import com.hanyeop.happysharing.util.ApiKey.Companion.FCM_KEY
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

//object로 싱글톤으로 객체를 생성한다.
//레트로핏의 3가지 구성요소 중 Retrofit.Builder 클래스 역할로 BASE_URL와  Converter를 설정해준다.
//여기서 둘다 by lazy 로 늦은 초기화 해줌으로써,
//api 변수가 사용될 때 초기화되고, 그 안에서 retrofit 변수를 사용하기 때문에 초기화 된다.
object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(FCM_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api : FcmInterface by lazy {
        retrofit.create(FcmInterface::class.java)
    }

    // Client
    private fun provideOkHttpClient(
        interceptor: AppInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .run {
            addInterceptor(interceptor)
            build()
        }

    // 헤더 추가 - 인증 토큰이 필요하기때문에 별도의 헤더를 추가해주어야 한다
    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain)
                : Response = with(chain) {
            val FCM_KEY = "AAAA1cdFMg8:APA91bEvGrHfN6ENhbaCUq0xz22ajNVXXKN-5rlWUVC26u2ycic4YTZ95_BRcNtUWo7QMA9gMOQAWN_vVhm5DsU0G5b0A3Gq5WhYzqug2PS87tErkghzsE4lBow5p2dixs5nVjlbvMSg"
            val newRequest = request().newBuilder()
                .addHeader("Authorization", "key=$FCM_KEY")
                .addHeader("Content-Type", "application/json")
                .build()
            proceed(newRequest)
        }
    }
}