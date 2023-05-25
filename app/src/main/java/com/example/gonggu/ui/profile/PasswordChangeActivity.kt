package com.example.gonggu.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.gonggu.MainActivity
import com.example.gonggu.databinding.ActivityPasswordChangeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PasswordChangeActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityPasswordChangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인증 초기화
        mAuth = Firebase.auth

        val completeBtn = binding.completeBtn

        completeBtn.setOnClickListener {
            changePassword()
        }
    }


    private fun changePassword() {
        val currentPassword = binding.currentPassword.text.toString()// 현재 비밀번호
        val passwordToChange = binding.passwordToChange.text.toString() // 변경할 비밀번호
        val passwordToChangeCheck = binding.passwordToChangeCheck.text.toString() // 변경할 비밀번호 확인

        if (currentPassword.isNotBlank() && passwordToChange.isNotBlank()
            && passwordToChangeCheck.isNotBlank()){
            if (passwordToChange == passwordToChangeCheck){
                mAuth.currentUser!!.updatePassword(passwordToChange).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@PasswordChangeActivity, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(
                            this@PasswordChangeActivity,
                            "비밀번호가 변경되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        Toast.makeText(this@PasswordChangeActivity,
                            "비밀번호 변경에 실패했습니다.",
                            Toast.LENGTH_SHORT).show()
                        println(task.exception.toString())
                    }
                }
            }
            else {
                Toast.makeText(this@PasswordChangeActivity,
                    "변경할 비밀번호가 일치하지 않습니다.",
                    Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(this@PasswordChangeActivity,
                "모든 칸을 입력해 주세요.",
                Toast.LENGTH_SHORT).show()
        }
    }
}