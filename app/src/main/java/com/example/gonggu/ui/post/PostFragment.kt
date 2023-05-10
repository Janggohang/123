package com.example.gonggu.ui.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gonggu.databinding.FragmentPostBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PostFragment : Fragment() {
    lateinit var binding: FragmentPostBinding
    val db = Firebase.database
    val postsRef = db.getReference("post")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)

        val summitBtn = binding!!.submitButton
        summitBtn.setOnClickListener {
            registerPost()
        }
        return binding!!.root
    }
    private fun registerPost() { // 게시물 등록
        val title = binding!!.titleEdit.text.toString() // 제목
        val price = binding!!.priceEdit.text.toString().toInt() // 가격
        val numOfPeople = binding!!.countEdit.text.toString().toInt() // 인원 수
        val content = binding!!.contentEdit.text.toString() // 내용
        val itemMap = hashMapOf(
            "content" to content,
            "numOfPeople" to numOfPeople,
            "price" to price,
            "title" to title
        )
        val postRef = postsRef.push()
        postRef.setValue(itemMap)
    }
}