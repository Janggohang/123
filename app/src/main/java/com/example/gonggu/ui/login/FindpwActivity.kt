package com.example.gonggu.ui.login

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.ActivityForgetpasswordBinding
import com.google.firebase.auth.FirebaseAuth

class FindpwActivity: AppCompatActivity()  {
    val binding by lazy { ActivityForgetpasswordBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val email = findViewById<EditText>(R.id.id_edit_text)

        binding.loginButton.setOnClickListener{
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this, "해당 이메일로 비밀번호 변경 요청을 전송했습니다.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "이메일 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}