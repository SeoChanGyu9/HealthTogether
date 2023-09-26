package com.example.firebase_test

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.firebase_test.databinding.ActivityMain2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//한번로그인하면 다음실행시 여기로
class MainActivity2 : AppCompatActivity() {

    private val firebaseViewModel : FirebaseViewModel by viewModels()
    private val fireStore = FirebaseFirestore.getInstance()
    private var otherDTO : UserDTO? = null



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMain2Binding.inflate(layoutInflater)

        setContentView(binding.root)

        var uId = FirebaseAuth.getInstance().currentUser?.uid

        //내상대정보 얻기
        fireStore.collection("match").document(uId!!).get()
            .addOnCompleteListener { documentSnapshot->
                otherDTO = documentSnapshot.result.toObject(UserDTO::class.java)
            }
        GlobalScope.launch {
            delay(3000)
        }

/*        //중복을 제외한 모든 캘린더 정보 가져오기
        var resultDTOs: ArrayList<UserDTO> = arrayListOf()
        fireStore.collection("calendar").document(uId!!).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            resultDTOs.clear()
            if (querySnapshot == null) {
                return@addSnapshotListener
            }

            // 데이터 받아오기
            for (snapshot in querySnapshot!!.collections) {
                var item = snapshot.toObject(UserDTO::class.java)
                resultDTOs.add(item!!)
            }
            notifyDataSetChanged()
        }*/




        // 채팅창으로
        binding.sendMessageButton.setOnClickListener {
            val intent = Intent(this, ChattingActivity::class.java)
            intent.putExtra("otherUid", otherDTO!!.uId.toString())
            startActivity(intent)
        }
        //내정보
        binding.infoButton.setOnClickListener {
            startActivity(Intent(this,Myinfo::class.java))
        }
        //매칭등록
        binding.matchingButton.setOnClickListener {
            firebaseViewModel.setmatching()
        }
        //매칭찾기
        binding.findmatchingButton.setOnClickListener {
            startActivity(Intent(this,Getmatching::class.java))
        }
        //gpt창이동
        binding.findmatchingButton.setOnClickListener {
            startActivity(Intent(this,gpt_mes_test::class.java))
        }
        //캘린더
        binding.calendarButton.setOnClickListener {
            startActivity(Intent(this,Calendar::class.java))
        }
        //커스텀캘린더
        binding.calendar2Button.setOnClickListener {
            startActivity(Intent(this,Calendar2::class.java))
        }
        //운동시작버튼
        binding.startButton.setOnClickListener {
            Log.d("로그","운동시작버튼 눌림")
            Log.d("로그","uId: "+uId)
            val time = System.currentTimeMillis()
            //val message = MessageDTO(uId,otherDTO!!.uId.toString(),"운동을 시작했습니다.",time)
            // FCM 전송하기 (푸쉬알림)
            val data = NotificationBody.NotificationData(getString(R.string.app_name)
                ,"운동 시작 알림","상대방이 운동을 시작했습니다.")
            val body = NotificationBody(otherDTO!!.token.toString(),data)
            firebaseViewModel.sendNotification(body)

            fireStore.collection("calendar").document(uId)
                .addSnapshotListener { documentSnapshot, _ ->
                    if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면

                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                    val formatted = current.format(formatter)

                    val calendarDTO = documentSnapshot.toObject(CalendarDTO::class.java)
                    //val newCalendarDTO = CalendarDTO("완료",calendarDTO!!.otherhelth.toString())
                    val newCalendarDTO = CalendarDTO("완료","미완료", formatted)

                    fireStore.collection("calendar").document(uId).collection("calendar").document(formatted).update("health","완료")
                    Log.d("로그","캘린더 접근")
                    //fireStore.collection("calendar").document(uId).collection("calendar").document(formatted).set(newCalendarDTO)
                    //fireStore.collection("calendar").document(uId).set(newCalendarDTO)
                }
        }
        //운동종료버튼
        binding.endButton.setOnClickListener {
            val time = System.currentTimeMillis()
            //val message = MessageDTO(uId,otherDTO!!.uId.toString(),"운동을 시작했습니다.",time)
            // FCM 전송하기 (푸쉬알림)
            val data = NotificationBody.NotificationData(getString(R.string.app_name)
                ,"운동 종료 알림","상대방이 운동을 종료했습니다.")
            val body = NotificationBody(otherDTO!!.token.toString(),data)
            firebaseViewModel.sendNotification(body)
        }



    }
}