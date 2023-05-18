package com.example.gonggu.ui.post

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gonggu.databinding.ActivityPostViewerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PostViewerActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = Firebase.firestore

    lateinit var mAuth: FirebaseAuth // 인증 객체
    //lateinit var mDbRef: DatabaseReference  // DB 객체
    //private val  postCollectionRef = mDbRef.child("post")
    //private val userdataCollectionRef =  mDbRef.child("userdata")
    val binding by lazy { ActivityPostViewerBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //Toast.makeText(this,"${currentPostIndex}의 글을 표시합니다.",Toast.LENGTH_SHORT).show()
        //binding.postContent.text = currentPost.content
        binding.postContent.text = intent.getStringExtra("content").toString()
        //binding.userName.text = currentPost.uid
        binding.userName.text = intent.getStringExtra("uId").toString()

        //binding.dateText.text = currentPost.time
        binding.dateText.text = intent.getStringExtra("time").toString()
        //binding.postLocation.text = currentPost.location
        binding.postLocation.text = intent.getStringExtra("location").toString()
        //binding.price2.text = currentPost.price.toString()
        binding.price2.text = intent.getStringExtra("price").toString()
        //binding.numOfPeople.text = currentPost.numOfPeople.toString()
        binding.numOfPeople.text = intent.getStringExtra("numOfPeople").toString()

        //ImageCacheManager.requestImage(currentPost.uid,binding.userImage)


        binding.returnButton.setOnClickListener {
            super.onBackPressed()
        }

    }
    companion object{
        lateinit var currentPost : PostData
    }
}