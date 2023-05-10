package com.example.gonggu.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gonggu.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    private lateinit var binding: ActivityChatBinding


    lateinit var mAuth: FirebaseAuth // 인증 객체
    lateinit var mDbRef: DatabaseReference  // DB 객체

    private lateinit var receiverRoom: String   // 받는 대화방
    private lateinit var senderRoom: String     //보낸 대화방

    private  lateinit var messageList: ArrayList<Message>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        messageList = ArrayList()
        val messageAdapter:MessageAdapter = MessageAdapter(this, messageList)

        //RecyclerView
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        //접속자 Uid
        val senderUid = mAuth.currentUser?.uid
        //보낸이방
        senderRoom = receiverUid + senderUid

        //받는이방
        receiverRoom = senderUid + receiverUid

        binding.chatOpponent.text = receiverName

        //메시지 전송 버튼
        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            val messageObject = Message(message, senderUid)

            mDbRef.child("chats").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                        //저장 성공하면
                        mDbRef.child("chats").child(receiverRoom).child("messages").push()
                            .setValue(messageObject)
            }
            //입력값 초기화
            binding.messageEditText.setText("")
        }

        //메시지 가져오기
        mDbRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for(postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    //적용
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        //뒤로가기
        binding.chatBack.setOnClickListener{
            super.onBackPressed()
        }


    }
}