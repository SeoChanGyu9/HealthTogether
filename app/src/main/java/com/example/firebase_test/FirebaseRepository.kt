package com.example.firebase_test

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import okhttp3.ResponseBody
import retrofit2.Response

//datasource를 캡슐화 , 응답을 확인할 라이브데이터 모음
//파이어베이스의 데이터들을 실시간으로 읽기 쓰기 작업
class FirebaseRepository() {

    val userDTO = MutableLiveData<UserDTO>() // 유저 정보

    // Firestore 초기화, Firebase에 접근하기 위한 객체
    private val fireStore = FirebaseFirestore.getInstance()
    val myResponse : MutableLiveData<Response<ResponseBody>> = MutableLiveData() // 메세지 수신 정보

    // 푸시 메세지 전송함수 / suspend fun : 일시중단 가능 함수 (서버로 전송)
    suspend fun sendNotification(notification: NotificationBody) {
        myResponse.value = RetrofitInstance.api.sendNotification(notification)
    }


    // 메시지 전송하기
    fun uploadChat(messageDTO: MessageDTO) {

        // 채팅 저장
        fireStore.collection("chat")
            .document(messageDTO.fromUid.toString())
            .collection(messageDTO.toUid.toString())
            .document(messageDTO.timestamp.toString())
            .set(messageDTO)
        fireStore.collection("chat")
            .document(messageDTO.toUid.toString())
            .collection(messageDTO.fromUid.toString())
            .document(messageDTO.timestamp.toString())
            .set(messageDTO)


    }

    // 프로필 불러오기
    //token은 회원가입시 고유값 가짐
    fun profileLoad(uid : String) {
        // FCM 불러오기 - 파이어베이스의 클라우드메시지의 토큰 부분 연결
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            // 실패시
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            // 받아온 새로운 토큰
            val token = task.result

            // 프로필 불러오기
            //addSnapshotListener: 문서를 수신대기상태로 만듦
            //users 컬렉션의 uid값에 해당하는 문서를 수신대기상태
            fireStore.collection("users").document(uid)
                .addSnapshotListener { documentSnapshot, _ ->
                    if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면

                    val userDTO = documentSnapshot.toObject(UserDTO::class.java)    //userDTO규격으로받기
                    if (userDTO?.userId != null) {  //이미 등록된 사용자

                        // 토큰이 변경되었을 경우 갱신
                        if (userDTO.token != token) {
                            Log.d("로그", "profileLoad: 토큰 변경되었음.")
                            val newUserDTO = UserDTO(userDTO.uId, userDTO.userId, token, userDTO.age, userDTO.height, userDTO.weight)
                            fireStore.collection("users").document(uid).set(newUserDTO)

                            // 유저정보 라이브데이터 변경하기
                            this.userDTO.value = newUserDTO
                        }

                        // 아니면 그냥 불러옴
                        else {
                            Log.d("로그", "profileLoad: 이미 동일한 토큰이 존재함.")
                            this.userDTO.value = userDTO!!
                        }
                    }
                }
        }
    }

    fun first_profileLoad(uid : String, nickname : String, height: String, weight: String, age: String) {
        // FCM 불러오기 - 파이어베이스의 클라우드메시지의 토큰 부분 연결
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            // 실패시
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            // 받아온 새로운 토큰
            val token = task.result

            // 프로필 불러오기
            //addSnapshotListener: 문서를 수신대기상태로 만듦
            //users 컬렉션의 uid값에 해당하는 문서를 수신대기상태
            fireStore.collection("users").document(uid)
                .addSnapshotListener { documentSnapshot, _ ->
                    if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면

                    val userDTO = documentSnapshot.toObject(UserDTO::class.java)    //userDTO규격으로받기
                    if (userDTO?.userId != null) {  //이미 등록된 사용자

                        // 토큰이 변경되었을 경우 갱신
                        if (userDTO.token != token) {
                            Log.d("로그", "profileLoad: 토큰 변경되었음.")
                            val newUserDTO = UserDTO(userDTO.uId, userDTO.userId, token, age, height, weight)
                            fireStore.collection("users").document(uid).set(newUserDTO)

                            // 유저정보 라이브데이터 변경하기
                            this.userDTO.value = newUserDTO
                        }

                        // 아니면 그냥 불러옴
                        else {
                            Log.d("로그", "profileLoad: 이미 동일한 토큰이 존재함.")
                            this.userDTO.value = userDTO!!
                        }
                    }

                    // 아이디 최초 생성 시
                    else if (userDTO?.userId == null) {
                        Log.d("로그", "아이디가 존재하지 않음")
                        val newUserDTO = UserDTO(uid, nickname, token, age, height, weight)
                        //파이어베이스 저장소에 users콜렉션에 나의uid문서에 내 정보 저장
                        fireStore.collection("users").document(uid).set(newUserDTO)

                        this.userDTO.value = newUserDTO
                    }
                }
        }
    }

    //매칭넣기
    fun setmatching(){
        var uId : String? = null
        uId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        fireStore.collection("users").document(uId)
            .addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면

                val userDTO = documentSnapshot.toObject(UserDTO::class.java)
                if (userDTO != null) {
                    fireStore.collection("matching").document(userDTO.uId!!).set(userDTO)
                }
            }
    }

    //매칭목록에서 삭제
    fun cancelmatching(uId : String){
        //var uId : String? = null
        //uId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        fireStore.collection("matching").document(uId).delete()
    }

    //프로필 불러오기
    fun getprofile(uid : String){
        fireStore.collection("users").document(uid)
            .addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면

                val userDTO = documentSnapshot.toObject(UserDTO::class.java)
                this.userDTO.value = userDTO!!
            }

    }

    // 프로필 수정하기
    fun profileEdit(userDTO: UserDTO) {
        this.userDTO.value = userDTO
        fireStore.collection("users").document(userDTO.uId!!).set(userDTO)
        Log.d("로그", "프로필 수정완료")
    }

    suspend fun getMatchingInfo():String{
        var uId : String? = null
        var otherUId : String? = null
        uId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("로그", "repos: "+uId)

        fireStore.collection("match").document(uId!!).get()
            .addOnCompleteListener { documentSnapshot->
                Log.d("로그", "안: "+otherUId)
                val userDTO = documentSnapshot.result.toObject(UserDTO::class.java)
                if (userDTO != null) {
                    otherUId = userDTO.uId.toString()
                    Log.d("로그", "otherUId: "+otherUId)

                }

                Log.d("로그", "userDTO: "+userDTO)
            }
        delay(1000L)
        return otherUId!!
    }
    //매칭된 나와 상대방 저장
    fun setMatchingInfo(userDTO : UserDTO, otherDTO : UserDTO){
        var uId : String? = null
        uId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val myMatchDTO = userDTO
        val otherMatchDTO = otherDTO

//        fireStore.collection("match").document(uId).set(otherUId)
//        fireStore.collection("match").document(otherUId).set(uId)

        fireStore.collection("users").document(uId)
            .addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면

                val userDTO = documentSnapshot.toObject(UserDTO::class.java)
                if (userDTO != null) {
                    fireStore.collection("match").document(uId).set(otherMatchDTO)
                }
            }

        fireStore.collection("users").document(otherDTO.uId.toString())
            .addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면

                val userDTO = documentSnapshot.toObject(UserDTO::class.java)
                if (userDTO != null) {
                    fireStore.collection("match").document(otherDTO.uId.toString()).set(myMatchDTO)
                }
            }
    }

}

