package com.example.gonggu.ui.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gonggu.MainActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.FragmentDeliveryBinding
import com.example.gonggu.databinding.ItemPostListBinding
import com.example.gonggu.ui.chat.RecyclerDecoration
import com.example.gonggu.ui.location.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.HashMap
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class DeliveryFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private val db = Firebase.database
    private val deliveryRef = db.getReference("delivery")
    private val userRef = db.getReference("user")
    var locationMap = HashMap<String, Double>() // 내 위도, 경도 정보 담을 hashmap

    // RecyclerView에 사용할 어댑터 객체와 데이터를 담을 ArrayList 선언
    private lateinit var mAdapter: DeliveryFragment.PostAdapter
    private val PostList: ArrayList<DeliveryData> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃 파일을 inflate하고 뷰 바인딩 객체를 생성
        val binding = FragmentDeliveryBinding.inflate(inflater, container, false)

        // MainActivity 객체 생성
        val mActivity = activity as MainActivity

        // 게시판 이동 후 네비게이션 바로 홈 화면 이동
        mActivity.addNavigation()

        // mAuth 객체 초기화
        mAuth = Firebase.auth

        // RecyclerView에 사용할 어댑터를 초기화
        mAdapter = PostAdapter(PostList)

        // RecyclerView 설정
        binding.recyclerViewDeliverylist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter }

        // 위치 정보 가져오기
        getMyLocation()

        val spaceDecoration = RecyclerDecoration(40)
        binding.recyclerViewDeliverylist.addItemDecoration(spaceDecoration)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = activity as MainActivity

        mainActivity.addNavigation()
    }

    private fun getMyLocation() {
        val user = userRef.child(mAuth.currentUser?.uid!!)

        user.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.value as Map <*,*>

                if (map["latitude"] != null && map["longitude"] != null){
                    locationMap["latitude"] = map["latitude"] as Double
                    locationMap["longitude"] = map["longitude"] as Double

                    loadPost()
                }
                else {
                    println("cannot get location")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    // 게시물 불러오기
    private fun loadPost() {
        // Firebase Realtime Database에서 데이터를 가져와서 RecyclerView에 표시
        deliveryRef.orderByChild("time").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPostList: ArrayList<DeliveryData> = ArrayList()

                if (locationMap["latitude"] != null && locationMap["longitude"] != null){
                    val myLocation = Location(locationMap["latitude"] as Double, locationMap["longitude"] as Double)

                    for (postSnapshot in snapshot.children) {
                        val post = postSnapshot.getValue(DeliveryData::class.java)

                        // 게시물의 위치 좌표
                        val postLocation = post?.let { Location(it.latitude, it.longitude) }
                        val distance = postLocation?.let { calculateDistance(myLocation, it) }

                        // 반경 5km 내의 게시물만 추가
                        if (distance != null) {
                            newPostList.add(0, post)
//                            if (distance <= 5) {
//                                newPostList.add(0, post)
//                            }
                        }
                    }
                }
                else {
                    Toast.makeText(requireContext(), "위치 정보를 설정해 주세요.", Toast.LENGTH_SHORT).show()
                }

                // 기존 리스트에 새로운 게시글 리스트를 맨 앞에 추가
                PostList.clear()
                PostList.addAll(newPostList)

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 처리할 작업을 구현
            }
        })
    }

    // 위도, 경도로 거리 계산
    fun calculateDistance(location1: Location, location2: Location): Double {
        val earthRadius = 6371 // 지구 반경 (단위: km)

        val latDiff = Math.toRadians(location2.latitude - location1.latitude)
        val lonDiff = Math.toRadians(location2.longitude - location1.longitude)

        val a = sin(latDiff / 2) * sin(latDiff / 2) +
                cos(Math.toRadians(location1.latitude)) * cos(Math.toRadians(location2.latitude)) *
                sin(lonDiff / 2) * sin(lonDiff / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    private inner class PostAdapter(private val postList: ArrayList<DeliveryData>) :
        RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPostListBinding.inflate(inflater, parent, false)
            return PostViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            val postData = postList[position]
            holder.bind(postData)

            holder.itemView.setOnClickListener{
//                PostViewerActivity.currentPost = postData
//                context?.startActivity(Intent(context,PostViewerActivity::class.java))

//                intent.putExtra("content",postData.content)
//                intent.putExtra("location",postData.location)
//                intent.putExtra("numOfPeople",postData.numOfPeople.toString())
//                intent.putExtra("price",postData.price.toString())
//                intent.putExtra("title",postData.title)
//                intent.putExtra("time",postData.time)
//                intent.putExtra("uId",postData.uid)
//                context?.startActivity(intent)
            }
        }

        override fun getItemCount() = postList.size

        inner class PostViewHolder(private val binding: ItemPostListBinding) :
            RecyclerView.ViewHolder(binding.root) {

            private val now = Calendar.getInstance()
            private val postDataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)

            fun bind(deliveryData: DeliveryData) {
                binding.itemPostTitle.text = deliveryData.title
                binding.itemPostPre.text = deliveryData.content
                // ImageView에 이미지 로드
                if( deliveryData.imageUrl.isNotEmpty()) {
                    Glide.with(binding.root)
                        .load(deliveryData.imageUrl)
                        .into(binding.itemPostImgList) // item_post_list.xml의 ImageView ID
                } else {
                    Glide.with(binding.root)
                        .load(R.drawable.image4)
                        .into(binding.itemPostImgList)
                }

                // 게시글 작성 시간 데이터 파싱
                val postTime = Calendar.getInstance().apply {
                    time = postDataFormat.parse(deliveryData.time)!!
                }

                // 날짜, 시간 변환
                val diff = now.timeInMillis - postTime.timeInMillis
                val timeString = when {
                    diff < 60 * 1000 -> "방금 전"
                    diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}분 전"
                    postTime.get(Calendar.YEAR) == now.get(Calendar.YEAR) -> {
                        when (postTime.get(Calendar.DAY_OF_YEAR)) {
                            now.get(Calendar.DAY_OF_YEAR) -> postDataFormat.format(postTime.time).substring(11)
                            else -> SimpleDateFormat("MM/dd", Locale.KOREA).format(postTime.time)
                        }
                    }
                    else -> SimpleDateFormat("yy/MM/dd", Locale.KOREA).format(postTime.time)
                }
                binding.itemPostListTime.text = timeString
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationMap.clear()
    }
}