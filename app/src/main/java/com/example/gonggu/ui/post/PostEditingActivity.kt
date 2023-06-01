package com.example.gonggu.ui.post

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.PermissionChecker
import com.bumptech.glide.Glide
import com.example.gonggu.R
import com.example.gonggu.databinding.ActivityPostEditingBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class PostEditingActivity : AppCompatActivity() {

    private lateinit var mDbRef: DatabaseReference
    private var selectedUri: Uri? = null
    private val binding by lazy { ActivityPostEditingBinding.inflate(layoutInflater) }
    private val storage: FirebaseStorage by lazy { Firebase.storage }
    private val DEFAULT_GALLERY_CODE = 2020

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val textWatcher = MyTextWatcher()

        // 가격과 인원 수에 따라 인당 가격 측정
        binding.priceEdit.addTextChangedListener(textWatcher)
        binding.countEdit.addTextChangedListener(textWatcher)

        // 게시물의 기존 정보 불러오기
        loadPostData()

        // 선택된 사진 삭제
        binding.selectedPhoto.setOnClickListener {
            binding.selectedPhoto.setImageDrawable(null)
            selectedUri = null
        }

        binding.photoButton.setOnClickListener {
            when {
                PermissionChecker.checkSelfPermission(
                    this@PostEditingActivity,
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

        // 게시물 수정 완료
        binding.submitBtn.setOnClickListener{
            // 선택된 사진이 있는 경우
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = { uri ->
                        editingPost(uri)
                    },
                    errorHandler = {
                        Toast.makeText(this@PostEditingActivity,
                            "사진 업로드에 실패했습니다!!!",
                            Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                editingPost("")
            }
            val intent = Intent(this@PostEditingActivity, PostViewerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun loadPostData() {
        binding.titleEdit.setText(currentPost.title) // 제목
        // ImageView에 이미지 로드
        if( currentPost.imageUrl.isNotEmpty()) {
            Glide.with(binding.root)
                .load(currentPost.imageUrl)
                .into(binding.selectedPhoto) // item_post_list.xml의 ImageView ID
        } else {
            Glide.with(binding.root)
                .load(R.drawable.photo_img)
                .into(binding.photoButton)
        }
        binding.priceEdit.setText(currentPost.price.toString()) // 가격
        binding.countEdit.setText(currentPost.numOfPeople.toString()) // 인원 수
        binding.pricePerText.text = (currentPost.price/currentPost.numOfPeople).toString() // 인당 가격
        binding.myLocation.text = currentPost.location
        binding.contentEdit.setText(currentPost.content)
    }

    private fun editingPost(imageUri: String) {
        val title = binding.titleEdit.text.toString() // 제목
        val price = binding.priceEdit.text.toString().toIntOrNull() // 가격
        val numOfPeople = binding.countEdit.text.toString().toIntOrNull() // 인원 수
        val content = binding.contentEdit.text.toString() // 내용
        val location = binding.myLocation.text.toString() // 위치
        val pricePerPerson = binding.pricePerText.text.toString().toIntOrNull() // 인당 가격
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) // 포맷 지정
        val currentTime = System.currentTimeMillis() // 현재 시간
        val time = dateFormat.format(currentTime) // 현재 시간을 포맷에 맞게 변환

        if (title.isEmpty() || price == null ||
            numOfPeople == null || content.isEmpty() || location.isEmpty()) {
            // 필수 입력값이 빠졌을 때
            Toast.makeText(this@PostEditingActivity,
                "모든 항목을 입력해 주세요.",
                Toast.LENGTH_SHORT).show()
            return
        }

        mDbRef = Firebase.database.reference
        val postRef = mDbRef.child("post").child(PostViewerActivity.currentPost.postId)
        // 게시글 수정
        postRef.get().addOnSuccessListener{ dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val postData = dataSnapshot.value as? HashMap<String, Any>
                    postData?.let { data ->
                        data["title"] = title
                        data["price"] = price
                        data["imageUrl"] = imageUri
                        data["numOfPeople"] = numOfPeople
                        data["content"] = content
                        data["pricePerPerson"] = pricePerPerson!!
                        data["time"] = time

                        postRef.updateChildren(data)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@PostEditingActivity,
                                    "게시글 수정이 완료됐습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this@PostEditingActivity,
                                    "게시글 수정 실패: $e",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
        }
        updateCurrentPost(content, numOfPeople, price, title, time, imageUri)
    }

    private fun updateCurrentPost(content: String, numOfPeople: Int, price: Int, title: String, time: String,
    imageUri: String) {
        currentPost.content = content
        currentPost.numOfPeople = numOfPeople
        currentPost.price = price
        currentPost.title = title
        currentPost.time = time
        currentPost.imageUrl = imageUri

        PostViewerActivity.currentPost = currentPost
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
                    binding.selectedPhoto.setImageURI(selectedImageUri)
                    selectedUri = selectedImageUri
                } else {
                    Toast.makeText(this@PostEditingActivity,
                        "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this@PostEditingActivity,
                    "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this@PostEditingActivity)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("확인") { _, _ ->
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1010)
            }
            .create()
            .show()
    }

    companion object {
        lateinit var currentPost : PostData
    }
}