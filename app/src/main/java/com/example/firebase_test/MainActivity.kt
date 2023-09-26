package com.example.firebase_test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.firebase_test.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.log

//앱 첫실행시 로그인
class MainActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    private val firebaseViewModel : FirebaseViewModel by viewModels()
    private var uId : String? = null
    private var nickname : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)



        // 계정 생성 버튼
        binding.signupOkButton.setOnClickListener {
            createAccount(binding.signupID.text.toString(),binding.signupPassword.text.toString(), binding.nickname.text.toString(), binding.height.text.toString(), binding.weight.text.toString(), binding.age.text.toString()) }
        //main2가기
        binding.main2Button.setOnClickListener {
            moveMainPage(auth?.currentUser)
        }

    }
    //켜자마자 로그인완료시 바로 main2로
    public override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }
    // id pw로 파이어베이스에 가입하고 정보 보냄
    private fun createAccount(email: String, password: String, nickname: String, height: String, weight: String, age: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {          // id pw창이 empty가 아니라면
            auth?.createUserWithEmailAndPassword(email, password)   //파이어베이스에 id pw로 계정생성
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //계정생성 완료
                        Log.d("로그","계정생성 완료")
                        uId = FirebaseAuth.getInstance().currentUser?.uid
                        firebaseViewModel.first_profileLoad(uId!!,nickname, height, weight, age)
                        moveMainPage(auth?.currentUser)
                    } else {
                        //계정생성 실패
                        Log.d("로그","계정생성 실패")
                    }
                }
        }
    }

    // 유저정보 넘겨주고 메인 액티비티 호출
    fun moveMainPage(user: FirebaseUser?){
        if( user!= null){
            startActivity(Intent(this,MainActivity2::class.java))
            finish()    //뷰종료
        }
    }

}