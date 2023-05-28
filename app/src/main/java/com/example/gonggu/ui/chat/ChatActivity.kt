package com.example.gonggu.ui.chat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gonggu.R
import com.example.gonggu.databinding.ActivityChatBinding
import com.example.gonggu.ui.post.PostData
import com.example.gonggu.ui.post.PostViewerActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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

        val chmDatabase = Firebase.database.reference.child("chats")


        chmDatabase.child(senderRoom).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapShot: DataSnapshot) {
                if(snapShot.exists()) {
                    val writerId = snapShot.child("writerId").children.first().value.toString()
                    if (writerId == mAuth.currentUser?.uid) {
                        // 현재 접속 계정과 writerId가 동일한 경우
                        binding.joinButton2.visibility = View.INVISIBLE
                        binding.joinAdmitButton2.visibility = View.VISIBLE
                    } else {
                        // 현재 접속 계정과 writerId가 동일하지 않은 경우
                        binding.joinButton2.visibility = View.VISIBLE
                        binding.joinAdmitButton2.visibility = View.INVISIBLE
                    }
                } else {
                    binding.joinButton2.visibility = View.INVISIBLE
                    binding.joinAdmitButton2.visibility = View.INVISIBLE
                }

            }


            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리
            }
        })




        // 참여하기
        binding.joinButton2.setOnClickListener {

            val message = "상대방이 참여 요청을 하였습니다. 승인을 원하시면 참여 승인을 눌러주세요."
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



        }

        //참여 승인 하기
        binding.joinAdmitButton2.setOnClickListener {

            //메시지 보내기


            val poDbRef = Firebase.database.reference.child("post")

            chmDatabase.child(senderRoom).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapShot: DataSnapshot) {
                    if(snapShot.exists()) {
                        val postWid = snapShot.child("postId").children.first().value.toString()
                        poDbRef.child(postWid).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val currentPost = snapshot.getValue(PostData::class.java)
                                    // postData 변수에 가져온 데이터가 매핑됩니다.

                                    val uid = receiverUid
                                    if (currentPost?.joiner!!.contains(uid)) {
                                        currentPost?.joiner!!.remove(uid)
                                        poDbRef.child(currentPost.postId).child("joiner").setValue(
                                            currentPost?.joiner!!)
                                            .addOnSuccessListener {
                                                val message = "상대방이 참여 요청을 거절 하였습니다."
                                                val messageObject = Message(message, senderUid, time, currentTime)
                                                mDbRef.child("chats").child(senderRoom)
                                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            mDbRef.child("chats").child(senderRoom).child("messages").push()
                                                                .setValue(messageObject).addOnSuccessListener { // 저장 성공하면
                                                                    mDbRef.child("chats").child(receiverRoom).child("messages")
                                                                        .push()
                                                                        .setValue(messageObject)
                                                                }
                                                        }

                                                        override fun onCancelled(databaseError: DatabaseError) {
                                                            // 오류 처리
                                                        }
                                                    })

                                                Toast.makeText(this@ChatActivity, "참여 요청을 취소했습니다.", Toast.LENGTH_SHORT).show()
//                        binding.likeCount.text = currentPost.like.size.toString()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this@ChatActivity, "참여 요청 취소에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                                currentPost?.joiner!!.add(uid) // 실패한 경우 좋아요를 다시 추가해줍니다.
//                        binding.likeCount.text = PostViewerActivity.currentPost.like.size.toString()
                                            }

                                    } else {
//                binding.likeSign.setImageResource(R.drawable.ic_full_heart)
                                        currentPost?.joiner!!.add(uid)
                                        poDbRef.child(currentPost.postId).child("joiner").setValue(
                                            currentPost?.joiner!!)
                                            .addOnSuccessListener {
                                                val message = "상대방이 참여 요청을 승인 하였습니다."
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

                                                Toast.makeText(this@ChatActivity, "참여 요청을 승인 하셨습니다.", Toast.LENGTH_SHORT).show()
//                        binding.likeCount.text = currentPost.like.size.toString()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this@ChatActivity, "참여 요청 승인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                                currentPost?.joiner!!.remove(uid)
//                        binding.likeCount.text = currentPost.like.size.toString()
                                            }
                                    }

                                    // TODO: 데이터 활용

                                } else {
                                    // 해당 postId에 해당하는 데이터가 없는 경우
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // 처리 중 오류가 발생한 경우의 콜백
                            }
                        })
                    }

                }


                override fun onCancelled(databaseError: DatabaseError) {
                    // 오류 처리
                }
            })








        }
        //뒤로가기
        binding.chatBack.setOnClickListener{
            super.onBackPressed()
        }
    }

    private fun getPostwuid(uid: String, callback: (String) -> Unit) {

        val mDbRefq = Firebase.database.reference.child("chats")
        val postRef = mDbRefq.child(uid)
        postRef.child("writerId")
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