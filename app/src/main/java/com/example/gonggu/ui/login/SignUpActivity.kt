package com.example.gonggu.ui.login

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gonggu.MainActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern
import kotlin.math.sign

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding

    lateinit var  mAuth: FirebaseAuth

    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화
        mAuth = Firebase.auth

        //인증 초기화
        mDbRef = Firebase.database.reference

        //회원가입 버튼
        binding.signinButton.setOnClickListener {
            //회원가입 제약
            if (binding.emailEditText.text.isBlank() || binding.nameEditText.text.isBlank() ||
                binding.passwordEditText.text.isBlank() || binding.passwordConfirmEditText.text.isBlank()){
                Toast.makeText(this,"빈칸이 남아있습니다. 빈칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.text).matches()){
                Toast.makeText(this,"올바른 이메일 형태가 아닙니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", binding.phoneEditText.text)){
                Toast.makeText(this,"올바른 전화번호 형태가 아닙니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.passwordConfirmEditText.text.toString() != binding.passwordEditText.text.toString()){
                Toast.makeText(this,"비밀번호가 서로 동일 하지 않습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val phonenumber = binding.phoneEditText.text.toString().trim()

            signUp(name, email, password, phonenumber)
        }
    }

    //회원가입
    private fun signUp(name: String, email: String, password:String, phonenumber:String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 성공시 실행
                    Toast.makeText(this,"회원가입 성공", Toast.LENGTH_SHORT).show()
                    val intent: Intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    startActivity(intent)
                    addUserToDatabase(name, email, phonenumber, mAuth.currentUser?.uid!!)
                } else {
                    // 실패시 실행
                    Toast.makeText(this,"회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase (name: String, email: String,  phonenumber: String, uId: String) {
        mDbRef.child("user").child(uId).setValue(UserData(name,email, phonenumber, uId))

    }

    }
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
