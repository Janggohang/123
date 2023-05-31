package com.example.gonggu.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gonggu.MainActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.FragmentSearchBinding
import com.example.gonggu.ui.chat.RecyclerDecoration
import com.example.gonggu.ui.post.DeliveryData
import com.example.gonggu.ui.post.PostAdapter
import com.example.gonggu.ui.post.PostData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {

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
        val binding = FragmentSearchBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity

        // 바텀 네비게이션바 사용
        mActivity.addNavigation()

        // RecyclerView에 사용할 어댑터를 초기화
        mAdapter = PostAdapter(requireContext(), postList)

        // RecyclerView 설정
        binding.recyclerViewSearchList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter }

        // 검색 시 처음에는 공동 구매 글이 뜨도록
        showSearchPost()

        // 뒤로 가기 버튼
        binding.returnButton.setOnClickListener {
            mActivity.onBackPressed()
        }

        // 검색 기능
        binding.search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                wordForSearch = binding.search.text.toString()
                showSearchPost()
                true
            } else {
                false
            }
        }

        // 검색한 내용이나 제목이 있는 공동 구매글 보기
        binding.showPost.setOnClickListener {
            showSearchPost()
        }

        // 검색한 내용이나 제목이 있는 공동 배달글 보기
        binding.showDelivery.setOnClickListener {
            showSearchDelivery()
        }

        val spaceDecoration = RecyclerDecoration(40)
        binding.recyclerViewSearchList.addItemDecoration(spaceDecoration)


        return binding.root
    }

    private fun showSearchPost() {
        mDatabase = Firebase.database.reference.child("post")
        // Firebase Realtime Database에서 데이터를 가져와서 RecyclerView에 표시
        mDatabase.orderByChild("time").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPostList: ArrayList<PostData> = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(PostData::class.java)
                    newPostList.add(0, post!!)
                }
                val searchPostList = mutableListOf<PostData>()

                for (post in newPostList) {
                    if (post.title.contains(wordForSearch) || post.content.contains(wordForSearch)) {
                        searchPostList.add(post)
                    }
                }

                // 기존 리스트에 새로운 게시글 리스트를 맨 앞에 추가
                postList.clear()
                postList.addAll(searchPostList)

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리할 작업을 구현
            }
        })
    }

    private fun showSearchDelivery() {
        mDatabase = Firebase.database.reference.child("delivery")
        mDatabase.orderByChild("time").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPostList: ArrayList<DeliveryData> = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(DeliveryData::class.java)
                    newPostList.add(0, post!!)
                }
                val searchDeliveryList = mutableListOf<DeliveryData>()

                for (post in newPostList) {
                    if (post.title.contains(wordForSearch) || post.content.contains(wordForSearch)) {
                        searchDeliveryList.add(post)
                    }
                }

                // 기존 리스트에 새로운 게시글 리스트를 맨 앞에 추가
                postList.clear()
                postList.addAll(searchDeliveryList)

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리할 작업을 구현
            }
        })
    }
    companion object {
        lateinit var wordForSearch : String
    }
}