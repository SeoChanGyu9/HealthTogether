package com.example.firebase_test

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.firebase_test.alarm.AlarmActivity
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
    private var uId =  FirebaseAuth.getInstance().currentUser?.uid.toString()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMain2Binding.inflate(layoutInflater)

        setContentView(binding.root)

        //내상대정보 얻기
        fireStore.collection("match").document(uId!!).get()
            .addOnCompleteListener { documentSnapshot->
                otherDTO = documentSnapshot.result.toObject(UserDTO::class.java)
            }
        GlobalScope.launch {
            delay(3000)
        }

        //매칭등록했는지 받아오기
        fireStore.collection("matching").document(uId!!)
            .addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면

                val userDTO = documentSnapshot.toObject(UserDTO::class.java)
                if (userDTO != null) {
                    binding.matchingButton.visibility = View.INVISIBLE
                    binding.unmatchingButton.visibility = View.VISIBLE
                }
            }





        //내정보
        binding.infoButton.setOnClickListener {
            startActivity(Intent(this,Myinfo::class.java))
        }
        //매칭등록
        binding.matchingButton.setOnClickListener {
            firebaseViewModel.setmatching()
            binding.matchingButton.visibility = View.INVISIBLE
            binding.unmatchingButton.visibility = View.VISIBLE
        }
        //매칭해제
        binding.unmatchingButton.setOnClickListener {
            firebaseViewModel.cancelmatching(uId)
            binding.unmatchingButton.visibility = View.INVISIBLE
            binding.matchingButton.visibility = View.VISIBLE
        }
        //매칭찾기
        binding.findmatchingButton.setOnClickListener {
            startActivity(Intent(this,Getmatching::class.java))
        }
        //gpt창이동
        binding.gptButton.setOnClickListener {
            startActivity(Intent(this,gpt_mes_test::class.java))
        }
        //알람 창이동
        binding.alarmButton.setOnClickListener {
            startActivity(Intent(this,AlarmActivity::class.java))
        }
        //커스텀캘린더
        binding.calendar2Button.setOnClickListener {
            val intent = Intent(this, Calendar2::class.java)
            if(otherDTO !=null)
                intent.putExtra("otheruid" , otherDTO!!.uId.toString() )
            startActivity(intent)
        }
        //운동시작버튼
        binding.startButton.setOnClickListener {
            Log.d("로그","운동시작버튼 눌림")
            Log.d("로그","uId: "+uId)
            Log.d("로그","otherUid: "+otherDTO?.uId)
            val time = System.currentTimeMillis()
            //val message = MessageDTO(uId,otherDTO!!.uId.toString(),"운동을 시작했습니다.",time)
            // FCM 전송하기 (푸쉬알림)
            val data = NotificationBody.NotificationData(getString(R.string.app_name)
                ,"운동 시작 알림","상대방이 운동을 시작했습니다.")
            val body = NotificationBody(otherDTO!!.token.toString(),data)
            firebaseViewModel.sendNotification(body)


            //캘린더 DB에 내 운동기록을 저장
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val formatted = current.format(formatter)
            var data2 = HashMap<String, Any>()
            data2.put("health","완료")
            data2.put("day",formatted)
            fireStore.collection("calendar").document(uId).collection("calendar").document(formatted)
                .set(data2)
                .addOnSuccessListener {
                    // 성공할 경우
                    Toast.makeText(this, "데이터가 추가되었습니다", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("MainActivity", "Error getting documents: $exception")
                }

/*            fireStore.collection("calendar").document(uId)
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
                }*/
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