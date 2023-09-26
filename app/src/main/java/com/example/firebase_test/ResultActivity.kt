package com.example.firebase_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_test.databinding.ActivityResultBinding
import com.example.firebase_test.databinding.ItemResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ResultActivity : AppCompatActivity() {
    private var fireStore : FirebaseFirestore? = null
    private var uid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityResultBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_result)
        setContentView(binding.root)

        uid = FirebaseAuth.getInstance().currentUser?.uid
        fireStore = FirebaseFirestore.getInstance()

        setContentView(R.layout.activity_result)
        binding.resultView.adapter = ResultViewRecyclerViewAdapter()
        binding.resultView.layoutManager = LinearLayoutManager(this)


    }

    inner class ResultViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var resultDTOs: ArrayList<ResultDTO> = arrayListOf()

        init {
            fireStore?.collection(uid!!)?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    resultDTOs.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    // 데이터 받아오기
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ResultDTO::class.java)
                        resultDTOs.add(item!!)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        }

        override fun getItemCount(): Int {
            return resultDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as CustomViewHolder).itemView

            //val resultTextOne = findViewById<TextView>(R.id.resultTextOne)
            //val resultTextTwo = findViewById<TextView>(R.id.resultTextOne)

            //resultTextOne.text = resultDTOs!![position].textOne
            //resultTextTwo.text = resultDTOs!![position].textOne
            //viewHolder.resultTextOne.text = resultDTOs!![position].textOne
            //viewHolder.resultTextTwo.text = resultDTOs!![position].textTwo

        }
    }
}