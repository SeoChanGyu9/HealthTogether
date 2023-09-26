package com.example.firebase_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_test.databinding.ItemMychatBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

//RecyclerView Adapter
class ChatAdapter(private val currentUid: String, private val otherUser: UserDTO)
    : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // 메시지 리스트
    private var chatList : ArrayList<MessageDTO> = arrayListOf() //MessageDTO규격에 맞는 배열 chatList 만듦
    var check = MutableLiveData<ArrayList<MessageDTO>>() // 최신 메시지 확인   / MutableLiveData - 값의 get set을 모두 가능

    // Firestore 초기화
    private val fireStore = FirebaseFirestore.getInstance()

    init {
        // 채팅 불러오기
        //파이어베이스 'chat'컬렉션 -> 사용자uid doc -> 상대방uid 컬렉션 ->정렬 시간기준 오름차순
        //addSnapshotListener: 파이어베이스 데이터 변경을 감지
        //Query.Direction.ASCENDING: 오름차순
        fireStore.collection("chat")
            .document(currentUid).collection(otherUser.uId.toString())
            .orderBy("timestamp",
                Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, _ ->

                if(querySnapshot == null) return@addSnapshotListener    //변동없으면 계속감지

                // 데이터 변경 감지 -> 문서 수신
                // 바뀐데이터만큼 반복
                for (doc in querySnapshot.documentChanges) {
                    // 문서가 추가될 경우 리사이클러뷰에 추가
                    if (doc.type == DocumentChange.Type.ADDED) {    //추가된 문서 doc 가 추가된 문서라면
                        var message = doc.document.toObject(MessageDTO::class.java) //doc 을 MessageDTO규격에 맞춰 오브젝트 생성
                        chatList.add(message)   //추가된 내용을 list에 추가
                    }
                }
                notifyDataSetChanged()  //recycler View를 새로고침
                check.value = chatList
            }
    }

    inner class ChatViewHolder(private val binding: ItemMychatBinding)
        : RecyclerView.ViewHolder(binding.root) {

        // 채팅 정보 바인딩
        fun bind(message : MessageDTO) {
            binding.apply {

                // 내가 한 채팅
                if(message.fromUid == currentUid){
                    myChat(binding,message)
                }

                // 상대방 채팅
                else if(message.fromUid == otherUser.uId.toString()){
                    otherChat(binding,message)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemMychatBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    // 내 채팅 표시
    private fun myChat(binding: ItemMychatBinding, message: MessageDTO){
        binding.apply {
            // 내 채팅 바인딩
            myMessageText.text = message.content
            //myTimeText.text = Utility.chatTimeConverter(message.timestamp!!)

            // 내 채팅 보이기
            myMessageText.visibility = View.VISIBLE
            myTimeText.visibility = View.VISIBLE

            // 상대방 채팅 가리기
            userIdText.visibility = View.GONE
            otherMessageText.visibility = View.GONE
            otherTimeText.visibility = View.GONE
            profileImageView.visibility = View.GONE
        }
    }

    // 상대방 채팅 표시
    private fun otherChat(binding: ItemMychatBinding, message: MessageDTO){
        binding.apply {
            // 상대방 채팅 바인딩
            userIdText.text = otherUser.userId
            otherMessageText.text = message.content
            //otherTimeText.text = Utility.chatTimeConverter(message.timestamp!!)
/*            Glide.with(profileImageView.context)
                .load(otherUser.imageUri)
                .placeholder(R.drawable.ic_baseline_person_24)
                .apply(RequestOptions().circleCrop())
                .into(profileImageView)*/

            // 상대방 채팅 보이기
            userIdText.visibility = View.VISIBLE
            otherMessageText.visibility = View.VISIBLE
            otherTimeText.visibility = View.VISIBLE
            profileImageView.visibility = View.VISIBLE

            // 내 채팅 가리기
            myMessageText.visibility = View.GONE
            myTimeText.visibility = View.GONE
        }
    }
}
