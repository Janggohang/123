package com.example.gonggu.ui.post

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker
import com.example.gonggu.MainActivity
import com.example.gonggu.databinding.FragmentPostBinding
import com.example.gonggu.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Suppress("DEPRECATION")
class PostFragment : Fragment() {
    lateinit var binding: FragmentPostBinding
    private val db = Firebase.database
    private var selectedUri: Uri? = null
    private val mAuth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val postsRef = db.getReference("post")
    private val usersRef = db.getReference("user")
    private val DEFAULT_GALLERY_CODE = 2020

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        val summitBtn = binding.submitButton
        val locationBtn = binding.locationBtn
        val addPhotoBtn = binding.photoButton

        // 사진 불러오기
        addPhotoBtn.setOnClickListener {
            when {
                PermissionChecker.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PermissionChecker.PERMISSION_GRANTED
                -> {
                    startContentProvider()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000)
                }
            }
        }

        // 위치 불러오기
        locationBtn.setOnClickListener {
            loadMyLocation()
        }

        // 게시글 등록
        summitBtn.setOnClickListener {
            registerPost()
        }

        return binding.root
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, DEFAULT_GALLERY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 예외처리
        if (resultCode != Activity.RESULT_OK)
            return

        when (requestCode) {
            DEFAULT_GALLERY_CODE -> {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    binding.photoButton.setImageURI(selectedImageUri)
                    selectedUri = selectedImageUri
                } else {
                    Toast.makeText(view?.context, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(view?.context, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(view?.context)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("확인") { _, _ ->
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
    }
    
    private fun registerPost() { // 게시물 등록
        val title = binding.titleEdit.text.toString() // 제목
        val price = binding.priceEdit.text.toString().toInt() // 가격
        val numOfPeople = binding.countEdit.text.toString().toInt() // 인원 수
        val content = binding.contentEdit.text.toString() // 내용
        val location = binding.myLocation.text.toString() // 위치

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
            "location" to location,
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
                    binding.myLocation.text = myAddress
                }
                else {
                    binding.myLocation.text = "내 위치를 설정해주세요."
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}