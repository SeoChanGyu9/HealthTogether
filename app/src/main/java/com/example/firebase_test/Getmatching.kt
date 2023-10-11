package com.example.firebase_test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_test.databinding.ActivityGetmatchingBinding
import com.example.firebase_test.databinding.ActivityMyinfoBinding
import com.example.firebase_test.databinding.ItemMychatBinding
import com.example.firebase_test.databinding.ItemResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Getmatching : AppCompatActivity() {

    private var firestore: FirebaseFirestore? = null
    private var uid: String? = null
    private lateinit var binding: ActivityGetmatchingBinding
    private val firebaseViewModel : FirebaseViewModel by viewModels()
    private var userDTO : UserDTO? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetmatchingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid = FirebaseAuth.getInstance().currentUser?.uid
        firestore = FirebaseFirestore.getInstance()

        binding.resultView.adapter = ResultViewRecyclerViewAdapter()
        binding.resultView.layoutManager = LinearLayoutManager(this)
    }

    inner class ResultViewRecyclerViewAdapter : RecyclerView.Adapter<ResultViewRecyclerViewAdapter.ResultViewHolder>() {
        var resultDTOs: ArrayList<UserDTO> = arrayListOf()

        init {
            //firestore?.collection("matching")?.orderBy("timestamp", Query.Direction.DESCENDING)
            firestore?.collection("matching")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    resultDTOs.clear()
                    if (querySnapshot == null) {
                        return@addSnapshotListener
                    }

                    // 데이터 받아오기
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(UserDTO::class.java)
                        //자신을 제외한 데이터 저장
                        if(item!!.uId != uid)
                            resultDTOs.add(item!!)

                        Log.d("로그", "resultDTOs: "+resultDTOs)

                    }
                    notifyDataSetChanged()
                }
            //내정보 가져오기
            firestore!!.collection("users").document(uid!!).get()
                .addOnCompleteListener { documentSnapshot->
                    userDTO = documentSnapshot.result.toObject(UserDTO::class.java)
                    Log.d("로그", "userDTO: "+userDTO)
                }


        }
        inner class ResultViewHolder(val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ResultViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_result, viewGroup, false)
            return ResultViewHolder(ItemResultBinding.bind(view))
        }


        override fun getItemCount(): Int {
            return resultDTOs.size
        }

        //findviewbyId대신 viewholder:binding으로 view access
        override fun onBindViewHolder(viewHolder: ResultViewHolder, position: Int) {//view->viewholder로
            //viewHolder.binding.todoText.text= myDataset[position].text //findViewById대신 viewholder.binding으로 접근
            viewHolder.binding.resultNickname.text = resultDTOs!![position].userId
            viewHolder.binding.resultAge.text = resultDTOs!![position].age
            viewHolder.binding.resultHeight.text = resultDTOs!![position].height
            viewHolder.binding.resultWeight.text = resultDTOs!![position].weight
            //val otheruId = resultDTOs!![position].uId.toString()
            val otheruId = resultDTOs!![position].uId.toString()

            //매칭버튼 누르기
            viewHolder.binding.buttonMatching.setOnClickListener{
                Log.d("로그11","otheruId: "+otheruId)
                firebaseViewModel.setMatchingInfo(userDTO!!, resultDTOs!![position])
                //매칭목록에서 삭제
                firebaseViewModel.cancelmatching(uid!!)
                firebaseViewModel.cancelmatching(otheruId!!)

                // FCM 전송하기 (푸쉬알림)
                val data = NotificationBody.NotificationData(getString(R.string.app_name)
                    ,"매칭 완료","매칭이 완료되었습니다.")
                val body = NotificationBody(resultDTOs!![position].token.toString(),data)
                firebaseViewModel.sendNotification(body)

                val intent = Intent(this@Getmatching, MainActivity2::class.java)
                startActivity(intent)

                finish()

            }
        }
    }




}