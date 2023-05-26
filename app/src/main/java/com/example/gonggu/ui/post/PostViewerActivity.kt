package com.example.gonggu.ui.post

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.gonggu.MainActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.ActivityPostViewer2Binding
import com.example.gonggu.databinding.ActivityPostViewerBinding
import com.example.gonggu.ui.chat.ChatActivity
import com.example.gonggu.ui.chat.Message
import com.example.gonggu.ui.profile.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PostViewerActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    private lateinit var storage : FirebaseStorage

    private lateinit var mDbRef: DatabaseReference

    //lateinit var mAuth: FirebaseAuth // 인증 객체
    //private val  postCollectionRef = mDbRef.child("post")
    //private val userdataCollectionRef =  mDbRef.child("userdata")
    val binding by lazy { ActivityPostViewer2Binding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        storage = Firebase.storage

        mAuth = Firebase.auth

        mDbRef = Firebase.database.reference

        var wname = ""
        getName(currentPost.writeruid) { name ->
            wname = name
        }

        //Toast.makeText(this,"${currentPostIndex}의 글을 표시합니다.",Toast.LENGTH_SHORT).show()

//        binding.postContent.text = intent.getStringExtra("content").toString()
//        binding.userName.text = intent.getStringExtra("uId").toString()
//        binding.dateText.text = intent.getStringExtra("time").toString()
//        binding.postLocation.text = intent.getStringExtra("location").toString()
//        binding.price2.text = intent.getStringExtra("price").toString()
//        binding.numOfPeople.text = intent.getStringExtra("numOfPeople").toString()
        binding.postContent.text = currentPost.content
        loadPhoto() // 게시글 이미지 불러오기

        getName(currentPost.writeruid) { name ->
            binding.userName.text = name
        }
//        mDbRef.child("user").child(currentPost.writeruid).child("name")
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        val name = snapshot.value as String
//                        binding.userName.text = name
//                    } else {
//                        // 데이터가 존재하지 않을 경우의 처리
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // 처리 중 오류가 발생한 경우의 콜백
//                }
//            })
        binding.dateText.text = currentPost.time
        binding.postLocation.text = currentPost.location
        binding.postPrice.text = currentPost.price.toString()
        binding.postNumOfPeople.text = currentPost.numOfPeople.toString()
        binding.likeCount.text = currentPost.like.size.toString()
        val profileImageRef = storage.reference.child("gonggu/userProfile/${currentPost.writeruid}.png")

        profileImageRef.metadata.addOnSuccessListener { metadata ->
            if (metadata.sizeBytes > 0) {
                // 프로필 사진이 존재하는 경우
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(binding.root.context)
                        .load(uri)
                        .into(binding.userImage)
                }.addOnFailureListener {
                    // 프로필 사진 로드 실패 시 처리할 내용
                }
            } else {
                // 프로필 사진이 존재하지 않는 경우
                Glide.with(binding.root.context)
                    .load(R.mipmap.default_user_image)
                    .into(binding.userImage)
            }
        }.addOnFailureListener {
            // 프로필 사진 정보 가져오기 실패 시 처리할 내용
            Glide.with(binding.root.context)
                .load(R.mipmap.default_user_image)
                .into(binding.userImage)
        }



        //ImageCacheManager.requestImage(currentPost.uid,binding.userImage)

        binding.likeButton.setOnClickListener {
            val uid = Firebase.auth.uid.toString()
            if (currentPost.like.contains(uid)) {
                binding.likeSign.setImageResource(R.drawable.ic_empty_heart)
                currentPost.like.remove(uid)
                mDbRef.child("post").child(currentPost.postId).child("like").setValue(currentPost.like)
                    .addOnSuccessListener {
                        Toast.makeText(this, "좋아요를 취소했습니다.", Toast.LENGTH_SHORT).show()
                        binding.likeCount.text = currentPost.like.size.toString()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "좋아요 취소에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        currentPost.like.add(uid) // 실패한 경우 좋아요를 다시 추가해줍니다.
                        binding.likeCount.text = currentPost.like.size.toString()
                    }

            } else {
                binding.likeSign.setImageResource(R.drawable.ic_full_heart)
                currentPost.like.add(uid)
                mDbRef.child("post").child(currentPost.postId).child("like").setValue(currentPost.like)
                    .addOnSuccessListener {
                        Toast.makeText(this, "이 글을 좋아요 하셨습니다.", Toast.LENGTH_SHORT).show()
                        binding.likeCount.text = currentPost.like.size.toString()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "좋아요에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        currentPost.like.remove(uid)
                        binding.likeCount.text = currentPost.like.size.toString()
                    }
            }
        }

        binding.joinButton.setOnClickListener {
            if (mAuth.currentUser?.uid != currentPost.writeruid) {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("name",wname)
                intent.putExtra("uId", currentPost.writeruid)

                startActivity(intent)

            } else {
                Toast.makeText(this@PostViewerActivity,"자신과는 대화할 수 없습니다.",Toast.LENGTH_SHORT).show()
            }
        }

        binding.returnButton.setOnClickListener {
            super.onBackPressed()
        }

        // 내가 쓴 게시글인 경우에만 메뉴 버튼 보이도록
        if (currentPost.writeruid == mAuth.currentUser!!.uid){
            binding.postMenu.visibility = View.VISIBLE

            binding.postMenu.setOnClickListener {
                showPopupMenu(it)
            }
        }
    }

    private fun loadPhoto() {
        // ImageView에 이미지 로드
        if( currentPost.imageUrl.isNotEmpty()) {
            Glide.with(binding.root)
                .load(currentPost.imageUrl)
                .into(binding.postImg) // item_post_list.xml의 ImageView ID
        } 
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.post_menu, popupMenu.menu)

        val postCollectionRef = mDbRef.child("post")

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit_post -> { // 게시글 수정
                    val intent = Intent(this@PostViewerActivity, PostEditingActivity::class.java)
                    startActivity(intent)
                    PostEditingActivity.currentPost = currentPost
                    true
                }
                R.id.delete_post -> { // 게시글 삭제
                    postCollectionRef.child(currentPost.postId).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(this@PostViewerActivity,
                                "게시글이 삭제 됐습니다.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@PostViewerActivity,
                                "게시글 삭제 실패 : $e", Toast.LENGTH_SHORT).show()
                        }
                    val intent = Intent(this@PostViewerActivity, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
    private fun getName(uid: String, callback: (String) -> Unit) {
        mDbRef = Firebase.database.reference.child("user")
        val userRef = mDbRef.child(uid)
        userRef.child("name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                var oname = ""
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var name = snapshot.value as String
                        oname = name

                    } else {
                        // 데이터가 존재하지 않을 경우의 처리
                    }
                    callback(oname)
                }

                override fun onCancelled(error: DatabaseError) {
                    // 처리 중 오류가 발생한 경우의 콜백
                    callback("") // 실패 시 빈 값 반환
                }
            })
    }


    companion object{
        lateinit var currentPost : PostData
    }
}