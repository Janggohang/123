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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

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

        if (title.isNullOrEmpty() || price == null || numOfPeople == null || content.isNullOrEmpty()) {
            // 필수 입력값이 빠졌을 때
            Toast.makeText(requireContext(), "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        //val time = System.currentTimeMillis().hours.toString() + ":" + System.currentTimeMillis().minutes.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) // 포맷 지정
        val currentTime = System.currentTimeMillis() // 현재 시간
        val time = dateFormat.format(currentTime) // 현재 시간을 포맷에 맞게 변환
        val uid = Firebase.auth.uid
        val like= mutableListOf<String>()
        val comment= mutableListOf<Map<String,String>>()
        val itemMap = hashMapOf(
            "content" to content,
            "numOfPeople" to numOfPeople,
            "price" to price,
            "title" to title,
            "time" to time,
            "uid" to uid
        )
        val postRef = postsRef.push()
        postRef.setValue(itemMap).addOnSuccessListener {
            Toast.makeText(requireContext(), "게시물이 등록되었습니다.", Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.popBackStack()
            //activity?.finish() // 현재 액티비티 종료
        } .addOnFailureListener{
            Toast.makeText(requireContext(), "게시물 등록에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        }



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