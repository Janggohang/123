package com.example.gonggu.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gonggu.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()

        binding.chatOpponent.text = receiverName


    }
}