package com.example.gonggu.ui.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gonggu.MainActivity
import com.example.gonggu.databinding.FragmentPostBinding
import com.example.gonggu.ui.home.HomeFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PostFragment : Fragment() {
    lateinit var binding: FragmentPostBinding
    private val db = Firebase.database
    private val mAuth = Firebase.auth
    private val postsRef = db.getReference("post")
    private val usersRef = db.getReference("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        val summitBtn = binding!!.submitButton // 등록 버튼
        val locationBtn = binding!!.locationBtn // 현재 위치 불러오기

        summitBtn.setOnClickListener {
            Toast.makeText(activity, "등록하였습니다.", Toast.LENGTH_SHORT).show()
            registerPost()

        }

        locationBtn.setOnClickListener {
            loadMyLocation()
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

    private fun loadMyLocation() { // 내 주소 불러오기
        usersRef.child(mAuth.currentUser?.uid!!).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.value as Map <*,*>
                val myAddress = map["address"].toString()
                if (myAddress != null){
                    binding!!.myLocation.text = myAddress
                }
                else {
                    binding!!.myLocation.text = "내 위치를 설정해주세요."
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}