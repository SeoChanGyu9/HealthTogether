package com.example.firebase_test

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : FirebaseRepository = FirebaseRepository()
    val myResponse = repository.myResponse
    val userDTO = repository.userDTO

    // 푸시 메세지 전송
    fun sendNotification(notification: NotificationBody) {
        viewModelScope.launch {
            repository.sendNotification(notification)
        }
    }

    // 프로필 불러오기
    fun profileLoad(uid : String){
        repository.profileLoad(uid)
    }
    // 첫 로그인
    fun first_profileLoad(uid : String, nickname : String, height: String, weight: String, age: String){
        repository.first_profileLoad(uid,nickname, height, weight, age)
    }
    //프로필 얻기
    fun getprofile(uid: String){
        repository.getprofile(uid)
    }

    // 메시지 전송하기
    fun uploadChat(messageDTO: MessageDTO) {
        repository.uploadChat(messageDTO)
    }
    //프로필 수정하기
    fun profileEdit(userDTO: UserDTO){
        repository.profileEdit(userDTO)
    }
    //매칭등록
    fun setmatching(){
        repository.setmatching()
    }
    //매칭등록해제
    fun cancelmatching(){
        repository.cancelmatching()
    }
    //현재 매칭된 상대정보가져오기
    suspend fun getMatchingInfo():String{
        Log.d("로그", "model: ")
        return repository.getMatchingInfo()
    }
    //매칭된 상대정보 저장
    fun setMatchingInfo(userDTO : UserDTO, otherDTO : UserDTO){
        Log.d("로그", "setMatchingInfo!")
        repository.setMatchingInfo(userDTO, otherDTO)
    }

}