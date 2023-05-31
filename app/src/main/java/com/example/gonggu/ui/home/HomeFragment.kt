package com.example.gonggu.ui.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gonggu.Data.ChatDataList
import com.example.gonggu.MainActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.FragmentHomeBinding
import com.example.gonggu.ui.chat.ChatListAdapter
import com.example.gonggu.ui.chat.RecyclerDecoration
import com.example.gonggu.ui.post.*
import com.example.gonggu.ui.search.SearchFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.*
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var mainContext : Context
    private lateinit var mDatabase: DatabaseReference

    // RecyclerView에 사용할 어댑터 객체와 데이터를 담을 ArrayList 선언
    private lateinit var mAdapter: PostAdapter
    private val postList: ArrayList<Any?> = ArrayList()
    var flag = 0
    companion object {
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mActivity = activity as MainActivity

        // RecyclerView에 사용할 어댑터를 초기화
        mAdapter = PostAdapter(requireContext(), postList)
        
        // 검색 기능
        binding.search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                SearchFragment.wordForSearch = binding.search.text.toString()
                mActivity.replaceFragment(SearchFragment())
                true
            } else {
                false
            }
        }
        // 공동 구매 게시판 이동
        binding.buy.setOnClickListener {
            mActivity.replaceFragment(BuyFragment())
        }

        showPopularPost()

        binding.chip.setOnClickListener {
            if (flag == 0){
                flag = 1
                binding.textView.text = "내 주변 실시간 인기 배달"
                binding.chip.text = "구매"
                showPopularDelivery()
            }
            else if (flag == 1) {
                flag = 0
                binding.textView.text = "내 주변 실시간 인기 구매"
                binding.chip.text = "배달"
                showPopularPost()
            }
        }
        // 글쓰기 버튼
        binding.fab.setOnClickListener {
            toggleFabMenu()
            //mActivity.replaceFragment(PostFragment())

        }
        binding.fab1.setOnClickListener {
            toggleFabMenu()
            mActivity.replaceFragment(PostFragment())

        }
        binding.fab2.setOnClickListener {
            toggleFabMenu()
            mActivity.replaceFragment(DeliveryPostFragment())
        }



        // 핫딜 사이트 이동
        binding.hotdeal.setOnClickListener {
            mActivity.replaceFragment(HotDealFragment())
        }
        // 해외 직구 사이트 이동
        binding.foreign.setOnClickListener {
            mActivity.replaceFragment(ForeignFragment())
        }
        // 공동 배달 게시판 이동
        binding.deliver.setOnClickListener {
            mActivity.replaceFragment(DeliveryFragment())
        }

        // RecyclerView 설정
        binding.recyclerViewRealtimelist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter }

        val spaceDecoration = RecyclerDecoration(40)
        binding.recyclerViewRealtimelist.addItemDecoration(spaceDecoration)
        mainContext = container!!.context

        return root
    }
    private fun toggleFabMenu() {
        val isOpen = binding.fab1.visibility == View.VISIBLE
        if (isOpen) {
            closeFabMenu()
        } else {
            showFabMenu()
        }
    }

    private fun showFabMenu() {
        binding.fab.animate().rotationBy(45f).setDuration(300).start()
        binding.fab1.animate().translationY(-resources.getDimension(R.dimen.fab_margin)).alpha(1f).setDuration(300).start()
        binding.fab2.animate().translationY(-resources.getDimension(R.dimen.fab_margin) * 2).alpha(1f).setDuration(300).start()

        binding.fab1.visibility = View.VISIBLE
        binding.fab2.visibility = View.VISIBLE
    }

    private fun closeFabMenu() {
        binding.fab.animate().rotationBy(-45f).setDuration(300).start()
        binding.fab1.animate().translationY(0f).alpha(0f).setDuration(300).start()
        binding.fab2.animate().translationY(0f).alpha(0f).setDuration(300).start()

        binding.fab1.visibility = View.INVISIBLE
        binding.fab2.visibility = View.INVISIBLE
    }

    // 인기 공동 구매 게시글 보기
    private fun showPopularPost() {
        mDatabase = Firebase.database.reference.child("post")

        // Firebase Realtime Database에서 데이터를 가져와서 RecyclerView에 표시
        mDatabase.orderByChild("time").addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPostList: ArrayList<PostData> = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(PostData::class.java)
                    newPostList.add(0, post!!)
                }
                val popularPostList = mutableListOf<PostData>()


                // 오늘 올라온 게시글 중 좋아요 개수가 5개 이상인 게시글만 추가
                for (post in newPostList) {
                    val postTime = post.time.split("-") // 게시글 등록 날짜 분리
                    val year = postTime[0].toInt()
                    val month = postTime[1].toInt()
                    val day = postTime[2].substringBefore(" ").toInt()

                    val today = LocalDate.now()
                    val postDate = LocalDate.of(year, month, day)
                    val comparison = today.compareTo(postDate)

                    if (comparison == 0 && post.like.size >= 5) {
                        popularPostList.add(post)
                    }
                }

                // 기존 리스트에 새로운 게시글 리스트를 맨 앞에 추가
                postList.clear()
                postList.addAll(popularPostList)

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리할 작업을 구현
            }
        })
    }

    // 인기 공동 배달 게시글 보기
    private fun showPopularDelivery() {
        mDatabase = Firebase.database.reference.child("delivery")

        // Firebase Realtime Database에서 데이터를 가져와서 RecyclerView에 표시
        mDatabase.orderByChild("time").addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPostList: ArrayList<DeliveryData> = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(DeliveryData::class.java)
                    newPostList.add(0, post!!)
                }
                val popularDeliveryList = mutableListOf<DeliveryData>()


                // 오늘 올라온 게시글 중 좋아요 개수가 5개 이상인 게시글만 추가
                for (post in newPostList) {
                    val postTime = post.time.split("-") // 게시글 등록 날짜 분리
                    val year = postTime[0].toInt()
                    val month = postTime[1].toInt()
                    val day = postTime[2].substringBefore(" ").toInt()

                    val today = LocalDate.now()
                    val postDate = LocalDate.of(year, month, day)
                    val comparison = today.compareTo(postDate)

                    if (comparison == 0 && post.like.size >= 5) {
                        popularDeliveryList.add(post)
                    }
                }

                // 기존 리스트에 새로운 게시글 리스트를 맨 앞에 추가
                postList.clear()
                postList.addAll(popularDeliveryList)

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리할 작업을 구현
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
    }
}