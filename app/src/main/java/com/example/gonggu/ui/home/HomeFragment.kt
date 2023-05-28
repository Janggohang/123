package com.example.gonggu.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gonggu.Data.ChatDataList
import com.example.gonggu.MainActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.FragmentHomeBinding
import com.example.gonggu.ui.chat.ChatListAdapter
import com.example.gonggu.ui.chat.RecyclerDecoration
import com.example.gonggu.ui.post.BuyFragment
import com.example.gonggu.ui.post.DeliveryFragment
import com.example.gonggu.ui.post.DeliveryPostFragment
import com.example.gonggu.ui.post.ForeignFragment
import com.example.gonggu.ui.post.HotDealFragment
import com.example.gonggu.ui.post.PostFragment

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var mainContext : Context
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
        val root: View = binding!!.root

        //val view = inflater.inflate(R.layout.fragment_home,container,false)
        //val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_realtimelist)
        //val wrBtn = view.findViewById<Button>(R.id.wrBtn) // 글쓰기 버튼
        val mActivity = activity as MainActivity
        binding!!.buy.setOnClickListener {
            mActivity.replaceFragment(BuyFragment())
        }
        binding!!.fabMain.setOnClickListener {
            //mActivity.replaceFragment(PostFragment())
            mActivity.replaceFragment(DeliveryPostFragment())
        }
        binding!!.hotdeal.setOnClickListener {
            mActivity.replaceFragment(HotDealFragment())
        }
        binding!!.foreign.setOnClickListener {
            mActivity.replaceFragment(ForeignFragment())
        }
        binding!!.deliver.setOnClickListener {
            mActivity.replaceFragment(DeliveryFragment())
        }
        binding!!.recyclerViewRealtimelist.layoutManager = LinearLayoutManager(requireContext())
        binding!!.recyclerViewRealtimelist.adapter = ChatListAdapter(ChatDataList)
        val spaceDecoration = RecyclerDecoration(40)
        binding!!.recyclerViewRealtimelist.addItemDecoration(spaceDecoration)
        mainContext = container!!.context
        return root //inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
    }
    data class Post(
        val postId: String,
        val title: String,
        val content: String,
        var views: Int,
        val timestamp: Long // 게시물 작성 시간을 나타내는 타임스탬프 변수

    )
    class PostManager {
        companion object {
            private const val POPULAR_THRESHOLD: Int = 500 // 인기 게시물로 판단하는 조회수 임계치
            private const val POPULAR_INTERVAL: Long = 6 * 60 * 60 * 1000 // 인기 게시물로 유지되는 시간 (6시간)
        }

        private val popularPosts: MutableList<Post> = mutableListOf()

        // 게시글 조회수 업데이트
        fun updateViews(postId: String, views: Int) {
            // postId에 해당하는 게시글을 popularPosts에서 찾아 조회수 업데이트
            for (post in popularPosts) {
                if (post.postId == postId) {
                    post.views = views
                    break
                }
            }

            // 조회수가 임계치를 넘었는지 확인하여 인기 게시물로 추가
            if (views > POPULAR_THRESHOLD) {
                addPopularPost(postId, views)
            }
        }

        // 인기 게시물로 추가
        private fun addPopularPost(postId: String, views: Int) {
            // 이미 인기 게시물 목록에 있는지 확인
            for (post in popularPosts) {
                if (post.postId == postId) {
                    return // 이미 인기 게시물에 있는 경우 추가하지 않음
                }
            }

            // 인기 게시물로 추가
            val timestamp = System.currentTimeMillis()
            val post = Post(postId, "", "", views, timestamp)
            popularPosts.add(post)
        }

        // 주기적으로 인기 게시물 목록에서 삭제
        private fun removeExpiredPopularPosts() {
            val iterator = popularPosts.iterator()
            while (iterator.hasNext()) {
                val post = iterator.next()
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - post.timestamp
                if (elapsedTime >= POPULAR_INTERVAL) {
                    iterator.remove()
                }
            }
        }

        // 인기 게시물 목록 반환
        fun getPopularPosts(): List<Post> {
            removeExpiredPopularPosts()
            return popularPosts
        }
    }

    class PostActivity : AppCompatActivity() {
        private lateinit var postManager: PostManager

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_post_viewer2)

            // PostManager 인스턴스 생성
            postManager = PostManager()

            // 게시글 데이터를 가져오는 로직
            val postId = intent.getStringExtra("postId")
            val views = 1000 // 예시로 조회수를 1000으로 설정

            // 게시글 조회수 업데이트
            if (postId != null) {
                postManager.updateViews(postId, views)
            }
        }
    }


}