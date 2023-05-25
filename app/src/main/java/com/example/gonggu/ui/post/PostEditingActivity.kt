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
import com.bumptech.glide.Glide
import com.example.gonggu.R
import com.example.gonggu.databinding.ActivityPostEditingBinding
import com.example.gonggu.databinding.ActivityPostViewer2Binding
import com.google.firebase.database.DatabaseReference

class PostEditingActivity : AppCompatActivity() {

    private lateinit var mDbRef: DatabaseReference
    private var selectedUri: Uri? = null
    val binding by lazy { ActivityPostEditingBinding.inflate(layoutInflater) }
    private val DEFAULT_GALLERY_CODE = 2020

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val textWatcher = MyTextWatcher()

        // 가격과 인원 수에 따라 인당 가격 측정
        binding.priceEdit.addTextChangedListener(textWatcher)
        binding.countEdit.addTextChangedListener(textWatcher)

        // 게시물의 기존 정보 불러오기
        loadPostData()
    }

    private fun loadPostData() {
        binding.titleEdit.setText(currentPost.title) // 제목
        // ImageView에 이미지 로드
        if( currentPost.imageUrl != null) {
            Glide.with(binding.root)
                .load(currentPost.imageUrl)
                .into(binding.photoButton) // item_post_list.xml의 ImageView ID
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