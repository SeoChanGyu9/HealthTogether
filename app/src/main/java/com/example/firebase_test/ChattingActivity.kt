package com.example.firebase_test

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.firebase_test.databinding.ActivityChattingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChattingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChattingBinding  //늦은초기화
    private var uId : String? = null
    //private var otherUId : String? = null




    // Firestore 초기화, Firebase에 접근하기 위한 객체
    private val fireStore = FirebaseFirestore.getInstance()

    // ListAdapter 선언
    private lateinit var chatAdapter: ChatAdapter

    // 뷰모델 연결 - viewModel은 repository에 있는 데이터를 관찰하고 있다가 변경이 되면 mutableData의 값을 변경시켜주는 역할을 합니다.
    private val firebaseViewModel : FirebaseViewModel by viewModels()


    // 현재 유저 닉네임
    private var curUserId = ""

    // 상대방 토큰
    private var token = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰바인딩
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // uid 불러오기
        uId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        // 내 프로필 불러오기
        firebaseViewModel.profileLoad(uId!!)

        // 유저 닉네임 동기화
        //Observer Callback은 LiveData(liveText)의 value의 변경을 감지하고 호출되는 부분
        //코틀린의 apply 함수를 사용하면 선언한 객체 이름을 생략하고 접근할 수 있음
        firebaseViewModel.userDTO.observe(this,{
            binding.apply {
                //curUserId = it.userId.toString()
                curUserId = it.userId.toString()
            }
        })




        //val otherUId = intent.getStringExtra("otherUid").toString()

        //val otherUId :Any
/*        if(uId == "kOMKKvnhlaYsBZx2Gg8iXdd8KEh1")
            otherUId ="4krYgyLWgEfgRHslc84zcaJSSun2"
        else
            otherUId ="kOMKKvnhlaYsBZx2Gg8iXdd8KEh1"*/

        val otherUId = intent.getStringExtra("otherUid").toString()

        Log.d("로그", "otherUId3: "+otherUId)

        binding.apply {
            // 채팅창이 공백일 경우 send 버튼 비활성화, 아니면 활성화
            messageEditView.addTextChangedListener { text->
                sendButton.isEnabled = text.toString() != ""
            }

            // 메시지 입력 시 리사이클러뷰 크기 조절
            messageRecyclerView.addOnLayoutChangeListener {
                    view, left, top, right, bottom, oldLeft, oldRight, oldTop, oldBottom ->
//                if (bottom > oldBottom && ::chatAdapter.isInitialized){
//                    messageRecyclerView.scrollToPosition(chatAdapter.itemCount-1)
//                }

                if(::chatAdapter.isInitialized){
                    messageRecyclerView.scrollToPosition(chatAdapter.itemCount-1)
                }
            }

            Log.d("로그", "uId: "+uId)
            //내 상대 uid불러오기
            //otherUId = firebaseViewModel.getMatchingInfo()

/*            fireStore.collection("match").document(uId!!).get()
                .addOnCompleteListener { documentSnapshot->
                    val userDTO = documentSnapshot.result.toObject(MatchDTO::class.java)
                    if (userDTO != null) {
                        otherUId = userDTO.otherUid.toString()
                        Log.d("로그", "otherUId: "+otherUId)

                    }

                    Log.d("로그", "userDTO: "+userDTO)
                }*/


                    Log.d("로그", "otherUId2: "+otherUId)
            // 상대 유저 정보 불러옴
            fireStore.collection("users").document(otherUId).get()
                .addOnCompleteListener { documentSnapshot->
                    Log.d("로그", "13241234: ")
                    if(documentSnapshot.isSuccessful){
                        val userDTO = documentSnapshot.result.toObject(UserDTO::class.java)
                        // 리사이클러뷰 어댑터 연결
                        chatAdapter = ChatAdapter(uId.toString(),userDTO!!) //내uID와 상대방DTO를 어뎁터에 변수로 넣음
                        messageRecyclerView.adapter = chatAdapter

                        userText.text = userDTO.userId  //상대방의닉네임
                        token = userDTO.token.toString()    //상대방의토큰

                        // 채팅 맨 밑으로 스크롤
                        chatAdapter.check.observe(this@ChattingActivity){
                            messageRecyclerView.scrollToPosition(chatAdapter.itemCount-1)
                        }
                    }
                }

            // 메시지 전송버튼
            sendButton.setOnClickListener {
                // 메세지 세팅
                val time = System.currentTimeMillis()
                val message = MessageDTO(uId,otherUId,messageEditView.text.toString(),time)
                // 메세지 전송
                firebaseViewModel.uploadChat(message)

                // FCM 전송하기 (푸쉬알림)
                val data = NotificationBody.NotificationData(getString(R.string.app_name)
                    ,curUserId,messageEditView.text.toString())

                val body = NotificationBody(token,data)
                firebaseViewModel.sendNotification(body)
                // 응답 여부
                firebaseViewModel.myResponse.observe(this@ChattingActivity){
                }

                // 전송 후 에디트뷰 초기화
                messageEditView.setText("")
            }
        }
    }
}
