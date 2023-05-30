package com.example.gonggu.ui.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gonggu.MainActivity
import com.example.gonggu.databinding.FragmentMyPostBinding
import com.example.gonggu.ui.chat.RecyclerDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyPostFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    // RecyclerView에 사용할 어댑터 객체와 데이터를 담을 ArrayList 선언
    private lateinit var mAdapter: PostAdapter
    private val postList: ArrayList<Any?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃 파일을 inflate하고 뷰 바인딩 객체를 생성
        val binding = FragmentMyPostBinding.inflate(inflater, container, false)
        // MainActivity 객체 생성
        val mActivity = activity as MainActivity

        // 게시판 이동 후 네비게이션 바로 홈 화면 이동
        mActivity.addNavigation()

        // FirebaseAuth와 Firebase Realtime Database 객체 초기화
        mAuth = Firebase.auth

        // RecyclerView에 사용할 어댑터를 초기화
        mAdapter = PostAdapter(requireContext(), postList)

        // RecyclerView 설정
        binding.recyclerViewPostlist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter }

        showMyPost()

        // 내가 쓴 공동 구매 게시글 보기
        binding.showPost.setOnClickListener {
            showMyPost()
        }
        // 내가 쓴 공동 배달 게시글 보기
        binding.showDelivery.setOnClickListener{
            showMyDelivery()
        }

        val spaceDecoration = RecyclerDecoration(40)
        binding.recyclerViewPostlist.addItemDecoration(spaceDecoration)

        return binding.root
    }

    private fun showMyPost() {
        mDatabase = Firebase.database.reference.child("post")
        // Firebase Realtime Database에서 데이터를 가져와서 RecyclerView에 표시
        mDatabase.orderByChild("time").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPostList: ArrayList<PostData> = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(PostData::class.java)

                    val postWriterUid = post?.let{ post->
                        post.writeruid
                    }
                    val uid = mAuth.currentUser?.uid!!

                    // 내가 쓴 게시물만 추가
                    if (post != null && postWriterUid == uid) {
                        newPostList.add(0, post)
                    }
                }

                // 기존 리스트에 새로운 게시글 리스트를 맨 앞에 추가
                postList.clear()
                postList.addAll(newPostList)

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리할 작업을 구현
            }
        })
    }

    private fun showMyDelivery() {
        mDatabase = Firebase.database.reference.child("delivery")
        // Firebase Realtime Database에서 데이터를 가져와서 RecyclerView에 표시
        mDatabase.orderByChild("time").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPostList: ArrayList<DeliveryData> = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(DeliveryData::class.java)

                    val postWriterUid = post?.let{ post->
                        post.writeruid
                    }
                    val uid = mAuth.currentUser?.uid!!

                    // 내가 쓴 게시물만 추가
                    if (post != null && postWriterUid == uid) {
                        newPostList.add(0, post)
                    }
                }

                // 기존 리스트에 새로운 게시글 리스트를 맨 앞에 추가
                postList.clear()
                postList.addAll(newPostList)

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리할 작업을 구현
            }
        })

    }
}