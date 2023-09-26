package com.example.firebase_test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.firebase_test.databinding.ActivityMyinfoBinding
import com.google.firebase.auth.FirebaseAuth

class Myinfo : AppCompatActivity() {

    private val firebaseViewModel : FirebaseViewModel by viewModels()
    private var uId : String? = null
    private lateinit var binding : ActivityMyinfoBinding

    // 현재 유저 정보들
    private var curUserId = ""
    private var curAge = ""
    private var curHeight = ""
    private var curWeight = ""

    private var curtoken = ""
    private var curuid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uId = FirebaseAuth.getInstance().currentUser?.uid
        firebaseViewModel.getprofile(uId!!)
                firebaseViewModel.userDTO.observe(this,{
                    binding.apply {
                curUserId = it.userId.toString()
                curAge = it.age.toString()
                curHeight = it.height.toString()
                curWeight = it.weight.toString()

                curtoken = it.token.toString()
                curuid = it.uId.toString()



                //binding.editTextToken.hint = curtoken
                //binding.editTextUid.hint = curuid
                binding.editTextUserid.setText(curUserId)
                binding.editTextAge.setText(curAge)
                binding.editTextHeight.setText(curHeight)
                binding.editTextWeight.setText(curWeight)
            }
        })

        binding.editButton.setOnClickListener {
            Log.d("로그","curuid: "+curuid)
            val newUserDTO = UserDTO(curuid, binding.editTextUserid.getText().toString(), curtoken, binding.editTextAge.getText().toString(), binding.editTextHeight.getText().toString(), binding.editTextWeight.getText().toString())
            firebaseViewModel.profileEdit(newUserDTO)
            startActivity(Intent(this,MainActivity2::class.java))
        }
    }
}
