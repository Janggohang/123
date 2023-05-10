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

//data class User (val profile :String, val name : String, val phonenumber : String, val email : String)
data class ChatData (val email:String, val name : String , val phonenumber : String, val uid: String){
    constructor(): this("","","","")
}

class ChatFragment : Fragment() {// FirebaseAuth와 Firebase Realtime Database 객체 선언
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    // RecyclerView에 사용할 어댑터 객체와 데이터를 담을 ArrayList 선언
    private lateinit var mAdapter: ChatAdapter
    private val mChatList: ArrayList<ChatData> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃 파일을 inflate하고 뷰 바인딩 객체를 생성
        val binding = FragmentChatBinding.inflate(inflater, container, false)

        // FirebaseAuth와 Firebase Realtime Database 객체 초기화
        mAuth = Firebase.auth
        mDatabase = Firebase.database.reference.child("user")

        // RecyclerView에 사용할 어댑터를 초기화
        mAdapter = ChatAdapter(mChatList)

        // RecyclerView 설정

        binding.recyclerViewChatlist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter }


        // Firebase Realtime Database에서 데이터를 가져와서 RecyclerView에 표시
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mChatList.clear()

                for (chatSnapshot in snapshot.children) {
                    // ChatData 객체로 변환하여 ArrayList에 추가
                    val chat = chatSnapshot.getValue(ChatData::class.java)
                    if(mAuth.currentUser?.uid != chat?.uid){
                        mChatList.add(chat!!)
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
                binding.itemLastChat.text = chatData.phonenumber
            }
        }
    }
}
