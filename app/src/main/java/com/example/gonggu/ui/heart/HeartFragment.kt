package com.example.gonggu.ui.heart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gonggu.MainActivity
import com.example.gonggu.databinding.FragmentHeartBinding
import com.example.gonggu.ui.chat.RecyclerDecoration
import com.example.gonggu.ui.post.DeliveryData
import com.example.gonggu.ui.post.PostAdapter
import com.example.gonggu.ui.post.PostData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

//data class User (val profile :String, val name : String, val phonenumber : String, val email : String)

class HeartFragment : Fragment() {// FirebaseAuth와 Firebase Realtime Database 객체 선언
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    // RecyclerView에 사용할 어댑터 객체와 데이터를 담을 ArrayList 선언
    private lateinit var mAdapter: PostAdapter
    private val postList: ArrayList<Any?> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃 파일을 inflate하고 뷰 바인딩 객체를 생성
        val binding = FragmentHeartBinding.inflate(inflater, container, false)

        // MainActivity 객체 생성
        val mActivity = activity as MainActivity

        // 게시판 이동 후 네비게이션 바로 홈 화면 이동
        mActivity.addNavigation()

        // FirebaseAuth와 Firebase Realtime Database 객체 초기화
        mAuth = Firebase.auth

        // RecyclerView에 사용할 어댑터를 초기화
        mAdapter = PostAdapter(requireContext(), postList)

        // RecyclerView 설정
        binding.recyclerViewHeartlist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter }

        showPost()

        // 좋아요한 공동 구매 게시글 보기
        binding.showPost.setOnClickListener {
            showPost()
        }

        // 좋아요한 공동 배달 게시글 보기
        binding.showDelivery.setOnClickListener {
            showDelivery()
        }

        val spaceDecoration = RecyclerDecoration(40)
        binding.recyclerViewHeartlist.addItemDecoration(spaceDecoration)


        return binding.root
    }

    private fun showPost() {
        mDatabase = Firebase.database.reference.child("post")
        // Firebase Realtime Database에서 데이터를 가져와서 RecyclerView에 표시
        mDatabase.orderByChild("time").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPostList: ArrayList<PostData> = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(PostData::class.java)
                    newPostList.add(0, post!!)
                }
                val heartPostList = mutableListOf<PostData>()

                for (post in newPostList) {
                    if (post.like?.contains(mAuth.currentUser?.uid) == true) {
                        heartPostList.add(post)
                    }
                }

                // 기존 리스트에 새로운 게시글 리스트를 맨 앞에 추가
                postList.clear()
                postList.addAll(heartPostList)

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리할 작업을 구현
            }
        })
    }

    private fun showDelivery() {
        mDatabase = Firebase.database.reference.child("delivery")
        mDatabase.orderByChild("time").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPostList: ArrayList<DeliveryData> = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(DeliveryData::class.java)
                    newPostList.add(0, post!!)
                }
                val heartPostList = mutableListOf<DeliveryData>()

                for (post in newPostList) {
                    if (post.like?.contains(mAuth.currentUser?.uid) == true) {
                        heartPostList.add(post)
                    }
                }

                // 기존 리스트에 새로운 게시글 리스트를 맨 앞에 추가
                postList.clear()
                postList.addAll(heartPostList)

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리할 작업을 구현
            }
        })
    }
}
