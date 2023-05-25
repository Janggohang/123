package com.example.gonggu.ui.chat

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gonggu.R
import com.example.gonggu.databinding.FragmentChatBinding
import com.example.gonggu.databinding.ItemChatListBinding
import com.example.gonggu.ui.login.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

//data class User (val profile :String, val name : String, val phonenumber : String, val email : String)
data class ChatData (val email:String, var name : String, val lastChat : String, var uid: String){
    constructor(): this("","","","")
}


class ChatFragment : Fragment() {// FirebaseAuth와 Firebase Realtime Database 객체 선언
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var chmDatabase: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private  lateinit var messageList: ArrayList<Message>
    private  lateinit var lach: String


    // RecyclerView에 사용할 어댑터 객체와 데이터를 담을 ArrayList 선언
    private lateinit var mAdapter: ChatAdapter
    private val mChatList: ArrayList<ChatData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = Firebase.storage
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃 파일을 inflate하고 뷰 바인딩 객체를 생성
        val binding = FragmentChatBinding.inflate(inflater, container, false)

        // FirebaseAuth와 Firebase Realtime Database 객체 초기화
        mAuth = Firebase.auth
        mDatabase = Firebase.database.reference.child("user")
        chmDatabase = Firebase.database.reference.child("chats")
//            .child(mAuth.currentUser?.uid+"gXbHLma48YfJH5sFhTzOzQaNN6I3")
//            .child("${mAuth.currentUser?.uid + mDatabase.key}")

        messageList = ArrayList()

        // RecyclerView에 사용할 어댑터를 초기화
        mAdapter = ChatAdapter(mChatList)

        // RecyclerView 설정

        binding.recyclerViewChatlist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter }


        // Firebase Realtime Database에서 데이터를 가져와서 RecyclerView에 표시
        chmDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mChatList.clear()

                for (chatSnapshot in snapshot.children.filter { it.key!!.substring(0,28).contains(mAuth.currentUser?.uid.toString()) }) {
                    // ChatData 객체로 변환하여 ArrayList에 추가

                    var uid = chatSnapshot.key.toString().replace(mAuth.currentUser?.uid.toString(), "")
                    val chat = chatSnapshot.getValue(ChatData::class.java)
                    var name : String


                    mChatList.add(chat!!)
                    mChatList[mChatList.indexOf(chat)].uid = uid
                    val nameRef = mDatabase.child(uid).child("name")
                    nameRef.get().addOnSuccessListener { nameSnapshot ->
                        if (nameSnapshot.exists()) {
                            name = nameSnapshot.value.toString()
                            mChatList[mChatList.indexOf(chat)].name = name
                            mAdapter.notifyDataSetChanged()
                        }
                    }.addOnFailureListener { exception ->
                        // Handle the failure
                        Log.e("firebase", "Error getting name data", exception)
                    }

                }

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리할 작업을 구현
            }
        })
        val spaceDecoration = RecyclerDecoration(40)
        binding.recyclerViewChatlist.addItemDecoration(spaceDecoration)


        return binding.root
    }

    private inner class ChatAdapter(private val chatList: ArrayList<ChatData>) :
        RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemChatListBinding.inflate(inflater, parent, false)
            return ChatViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
            val chatData = chatList[position]
            holder.bind(chatData)

            holder.itemView.setOnClickListener{
                val intent = Intent(context, ChatActivity::class.java)

                intent.putExtra("name",chatData.name)
                intent.putExtra("uId",chatData.uid)

                context?.startActivity(intent)
            }
        }

        override fun getItemCount() = chatList.size

        inner class ChatViewHolder(private val binding: ItemChatListBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(chatData: ChatData) {
                binding.itemNameChatList.text = chatData.name
                getLastChatMessage(chatData.uid) { lastChatMessage ->
                    binding.itemLastChat.text = lastChatMessage
                }
//                binding.itemChatListTime.text = getLastChatTime(chatData.uid)

                val profileImageRef = storage.reference.child("gonggu/userProfile/${chatData.uid}.png")

                profileImageRef.metadata.addOnSuccessListener { metadata ->
                    if (metadata.sizeBytes > 0) {
                        // 프로필 사진이 존재하는 경우
                        profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(binding.root.context)
                                .load(uri)
                                .into(binding.itemImageChatList)
                        }.addOnFailureListener {
                            // 프로필 사진 로드 실패 시 처리할 내용
                        }
                    } else {
                        // 프로필 사진이 존재하지 않는 경우
                        Glide.with(binding.root.context)
                            .load(R.mipmap.default_user_image)
                            .into(binding.itemImageChatList)
                    }
                }.addOnFailureListener {
                    // 프로필 사진 정보 가져오기 실패 시 처리할 내용
                    Glide.with(binding.root.context)
                        .load(R.mipmap.default_user_image)
                        .into(binding.itemImageChatList)
                }
            }

            private fun getLastChatMessage(uid: String, callback: (String) -> Unit) {
                val roomid = uid + mAuth.currentUser?.uid
                val lastMessageRef = mDatabase.child("chats").child(roomid).child("messages").limitToLast(1)

                lastMessageRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var lastChatMessage = "" // 초기화된 값

                        for (postSnapshot in snapshot.children) {
                            val message = postSnapshot.getValue(Message::class.java)
                            lastChatMessage = message?.message.toString()
                        }

                        // 마지막 채팅 메시지를 반환
                        callback(lastChatMessage)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // 실패 시 처리할 작업을 구현
                        callback("") // 실패 시 빈 값 반환
                    }
                })
            }


            private fun getLastChatTime(uid: String): String {
                // TODO: uid에 해당하는 채팅 데이터를 가져와 마지막 채팅 시간 반환
                // 여기에 실제로 데이터베이스에서 채팅 데이터를 조회하고 마지막 채팅 시간을 반환하는 로직을 구현해야 합니다.
                // 현재는 임시로 "12:34"를 반환하도록 처리하였습니다.
                return "12:34"
            }
        }


    }
}
