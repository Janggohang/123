package com.example.gonggu.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gonggu.databinding.ActivityOptionBinding
import com.example.gonggu.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OptionActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = Firebase.auth

        // 뒤로가기버튼
        binding.backButton.setOnClickListener {
            super.onBackPressed()
        }
        
        // 개인정보 변경
        binding.profileChange.setOnClickListener {
            // 개인정보 수정 기능 추가
            val intent = Intent(this, ProfileSettingActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 변경
        binding.passwordchange.setOnClickListener {
            val intent = Intent(this, PasswordChangeActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 버튼
        binding.logout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            mAuth.signOut()
            startActivity(intent)
        }
        //완료 버튼
        binding.completeButton.setOnClickListener {
            super.onBackPressed()
        }
    }
}