package com.example.gonggu.ui.post

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    var locationMap = HashMap<String, Double>() // 내 위도, 경도 정보 담을 hashmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        val textWatcher = MyTextWatcher()
        val summitBtn = binding.submitBtn
        val locationBtn = binding.locationBtn
        val addPhotoBtn = binding.photoButton
        val priceText = binding.priceEdit
        val numOfPeopleText = binding.countEdit

        // 가격과 인원 수에 따라 인당 가격 측정
        priceText.addTextChangedListener(textWatcher)
        numOfPeopleText.addTextChangedListener(textWatcher)

        // 사진 불러오기
        addPhotoBtn.setOnClickListener {
            when {
                PermissionChecker.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PermissionChecker.PERMISSION_GRANTED
                -> {
                    startContentProvider()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
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
            // 선택된 사진이 있는 경우
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = { uri ->
                        registerPost(uri)
                    },
                    errorHandler = {
                        Toast.makeText(requireContext(), "사진 업로드에 실패했습니다!!!", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                registerPost("")
            }
        }

        return binding.root
    }

    private fun registerPost(imageUrl : String) { // 게시물 등록
        val title = binding.titleEdit.text.toString() // 제목
        val price = binding.priceEdit.text.toString().toIntOrNull() // 가격
        val numOfPeople = binding.countEdit.text.toString().toIntOrNull() // 인원 수
        val pricePerPerson = binding.pricePerText.text.toString().toIntOrNull() // 인당 가격
        val content = binding.contentEdit.text.toString() // 내용
        val location = binding.myLocation.text.toString() // 위치
        val latitude = locationMap["latitude"] as Double // 위도
        val longitude = locationMap["longitude"] as Double // 경도

        if (title.isEmpty() || price == null ||
            numOfPeople == null || content.isEmpty() || location == null) {
            // 필수 입력값이 빠졌을 때
            Toast.makeText(requireContext(), "모든 항목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        else if (location == "내 위치를 설정해 주세요"){
            Toast.makeText(requireContext(), "내 위치를 설정 해야 게시글 등록이 가능 합니다.", Toast.LENGTH_SHORT).show()
            return
        }
        //val time = System.currentTimeMillis().hours.toString() + ":" + System.currentTimeMillis().minutes.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) // 포맷 지정
        val currentTime = System.currentTimeMillis() // 현재 시간
        val time = dateFormat.format(currentTime) // 현재 시간을 포맷에 맞게 변환
        val writeruid = Firebase.auth.uid!!
        val like = mutableListOf<String>()
        val comment= mutableListOf<Map<String,String>>()

        val postRef = postsRef.push()

        // 게시물 데이터베이스
        val postItem = pricePerPerson?.let {
            PostData(content, latitude, location, longitude,
                numOfPeople, price, title,time,
                writeruid,imageUrl, like, postRef.key!!,
                it
            )
        }

        postRef.setValue(postItem).addOnSuccessListener {
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
                if (map.containsKey("address")){
                    val myAddress = map["address"].toString()
                    locationMap["latitude"] = map["latitude"] as Double
                    locationMap["longitude"] = map["longitude"] as Double
                    binding.myLocation.text = myAddress
                } else {
                    binding.myLocation.text = "내 위치를 설정해 주세요."
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //storage에 사진 업로드 함수
    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("gonggu/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.reference.child("gonggu/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
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
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1010)
            }
            .create()
            .show()
    }

    private inner class MyTextWatcher: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 텍스트 변경 전에 호출
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 텍스트 변경 중에 호출
            val price = binding.priceEdit.text.toString().toIntOrNull() // 가격
            val numOfPeople = binding.countEdit.text.toString().toIntOrNull() // 인원 수

            if (price != null && numOfPeople != null) {
                binding.pricePerText.text = (price / numOfPeople).toString()
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // 텍스트 변경 후에 호출
        }

    }
}