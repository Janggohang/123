package com.example.gonggu.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gonggu.MainActivity
import com.example.gonggu.databinding.FragmentDeliveryBinding
import com.example.gonggu.ui.chat.RecyclerDecoration
import com.example.gonggu.ui.location.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.HashMap
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
    private lateinit var mAdapter: PostAdapter
    private val postList: ArrayList<Any?> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃 파일을 inflate하고 뷰 바인딩 객체를 생성
        val binding = FragmentDeliveryBinding.inflate(inflater, container, false)

        val category1 = binding.btnCategory1 // 한식 카테고리
        val category2 = binding.btnCategory2 // 중식 카테고리
        val category3 = binding.btnCategory3 // 양식 카테고리
        val category4 = binding.btnCategory4 // 일식 카테고리
        val category5 = binding.btnCategory5 // 패스트푸드 카테고리
        val category6 = binding.btnCategory6 // 기타 카테고리
        val viewByCategory = ViewByCategory()

        category1.setOnClickListener(viewByCategory)
        category2.setOnClickListener(viewByCategory)
        category3.setOnClickListener(viewByCategory)
        category4.setOnClickListener(viewByCategory)
        category5.setOnClickListener(viewByCategory)
        category6.setOnClickListener(viewByCategory)

        // mAuth 객체 초기화
        mAuth = Firebase.auth

        // RecyclerView에 사용할 어댑터를 초기화
        mAdapter = PostAdapter(requireContext(), postList)

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

                        // 반경 3km 내의 게시물만 추가
                        if (distance != null) {
                            if (distance <= 5) {
                                newPostList.add(0, post)
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(requireContext(), "위치 정보를 설정해 주세요.", Toast.LENGTH_SHORT).show()
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

    // 카테고리 별로 배달 글 보기
    private inner class ViewByCategory: View.OnClickListener {
        override fun onClick(view: View?) {
            if (view is Button) {
                val category = view.text.toString()

                deliveryRef.orderByChild("time").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newPostList: ArrayList<DeliveryData> = ArrayList()

                        if (locationMap["latitude"] != null && locationMap["longitude"] != null){
                            val myLocation = Location(locationMap["latitude"] as Double, locationMap["longitude"] as Double)

                            for (postSnapshot in snapshot.children) {
                                val post = postSnapshot.getValue(DeliveryData::class.java)

                                // 게시글의 위치 좌표
                                val postLocation = post?.let { Location(it.latitude, it.longitude) }
                                val distance = postLocation?.let { calculateDistance(myLocation, it) }
                                // 게시글의 카테고리
                                val postCategory = post?.let {it.category}

                                // 반경 3km 내의 게시물만 추가
                                if (distance != null && postCategory == category) {
                                    if (distance <= 3) {
                                        newPostList.add(0, post)
                                    }
                                }
                            }
                        }
                        else {
                            Toast.makeText(requireContext(), "위치 정보를 설정해 주세요.", Toast.LENGTH_SHORT).show()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationMap.clear()
    }
}