package com.example.gonggu.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gonggu.MainActivity
import com.example.gonggu.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //자동 로그인
//
//        if (Firebase.auth.currentUser != null){
//            //Toast.makeText(this,"Current user is ${Firebase.auth.uid}",Toast.LENGTH_SHORT).show()
//            startActivity(
//                Intent(this, MainActivity::class.java)
//            )
//            finish()
//        }

        //로그인 이벤트
        binding.loginButton.setOnClickListener {
            doLogin(binding.idEditText.text.toString(),binding.passwordEditText.text.toString())
        }

        //회원가입 버튼 이벤트
        binding.createAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SignUpActivity::class.java))
        }

        // 비밀번호 찾기 버튼 이벤트
        binding.findpw.setOnClickListener {
            val intent = Intent(this,FindpwActivity::class.java)
            startActivity(intent)
        }

        println("Login activity activated.")
    }

    private fun doLogin(id: String, pass: String)
    {
        if (id.isBlank()) {
            Toast.makeText(this, "로그인 정보를 확인하세요.", Toast.LENGTH_SHORT).show()
            binding.passwordEditText.text = null
            return
        }

        if (pass.isBlank()) {
            Toast.makeText(this, "로그인 정보를 확인하세요.", Toast.LENGTH_SHORT).show()
            binding.passwordEditText.text = null
            return
        }

        Firebase.auth.signInWithEmailAndPassword(id,pass)
            .addOnCompleteListener(this) {
                println("Login Task occur")

                if(it.isSuccessful){
                    Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this,MainActivity::class.java)
                    )
                    finish()
                }

                else{
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "로그인 정보를 확인하세요.", Toast.LENGTH_SHORT).show()
                    binding.passwordEditText.text = null
                }
            }

    }
}