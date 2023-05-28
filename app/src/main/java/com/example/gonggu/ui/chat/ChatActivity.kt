package com.example.gonggu.ui.chat

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gonggu.databinding.ActivityChatBinding
import com.example.gonggu.ui.post.PostData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class ChatActivity : AppCompatActivity() {

    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    private lateinit var postUid: String
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
        postUid = intent.getStringExtra("postId").toString()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        //접속자 Uid
        val senderUid = mAuth.currentUser?.uid
        //보낸이방
        senderRoom = receiverUid + senderUid

        //받는이방
        receiverRoom = senderUid + receiverUid

        binding.chatOpponent.text = receiverName

        // 메시지 전송 시간
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val time = dateFormat.format(Date(currentTime))

        //메시지 전송 버튼
        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            val messageObject = Message(message, senderUid, time, currentTime)
            mDbRef.child("chats").child(senderRoom)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.child("postId").exists()) {
                            // postId가 존재하는 경우

                            mDbRef.child("chats").child(senderRoom).child("messages").push()
                                .setValue(messageObject).addOnSuccessListener { // 저장 성공하면
                                    mDbRef.child("chats").child(receiverRoom).child("messages")
                                        .push()
                                        .setValue(messageObject)
                                }
                        } else {
                            // postId가 존재하지 않는 경우
                            //
                            mDbRef.child("chats").child(senderRoom).child("postId").push()
                                .setValue(postUid).addOnSuccessListener { // 저장 성공하면
                                    mDbRef.child("chats").child(receiverRoom).child("postId").push()
                                        .setValue(postUid)
                                    //
                                    mDbRef.child("chats").child(senderRoom).child("writerId").push()
                                        .setValue(receiverUid).addOnSuccessListener { // 저장 성공하면
                                            mDbRef.child("chats").child(receiverRoom).child("writerId")
                                                .push()
                                                .setValue(receiverUid)
                                        }
                                    //
                                    mDbRef.child("chats").child(senderRoom).child("messages").push()
                                        .setValue(messageObject).addOnSuccessListener { // 저장 성공하면
                                            mDbRef.child("chats").child(receiverRoom).child("messages")
                                                .push()
                                                .setValue(messageObject)
                                        }
                                }

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // 오류 처리
                    }
                })


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




        mDbRef.child("chats").child(senderRoom)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child("writerId").toString() == mAuth.currentUser?.uid) {
                        // postId가 존재하는 경우
                        binding.joinButton2.visibility = View. INVISIBLE
                        binding.joinAdmitButton2.visibility = View.VISIBLE
                    } else {
                        // postId가 존재하지 않는 경우
                        binding.joinButton2.visibility = View. VISIBLE
                        binding.joinAdmitButton2.visibility = View.INVISIBLE

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 오류 처리
                }
            })



        // 참여하기
        binding.joinButton2.setOnClickListener {



        }

        //참여 승인 하기
        binding.joinAdmitButton2.setOnClickListener {



        }
        //뒤로가기
        binding.chatBack.setOnClickListener{
            super.onBackPressed()
        }
    }

    private fun getPostwuid(uid: String, callback: (String) -> Unit) {

        val mDbRefq = Firebase.database.reference.child("post")
        val postRef = mDbRefq.child(uid)
        postRef.child("writeruid")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                var wuid = ""
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var uid = snapshot.value as String
                        wuid = uid

                    } else {
                        // 데이터가 존재하지 않을 경우의 처리
                    }
                    callback(wuid)
                }

                override fun onCancelled(error: DatabaseError) {
                    // 처리 중 오류가 발생한 경우의 콜백
                    callback("") // 실패 시 빈 값 반환
                }
            })
    }

    companion object{
        lateinit var currentPost : PostData
    }
}