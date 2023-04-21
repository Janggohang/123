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


        binding.signinButton.setOnClickListener {

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

    private fun addUserToDatabase (name: String, email: String, uId: String, phonenumber: String) {
        mDbRef.child("user").child(uId).setValue(UserData(name,email, uId,phonenumber))
    }



//    private val db: FirebaseFirestore = Firebase.firestore
//    private val userdataCollectionRef = db.collection("userdata")
//    private val storage = Firebase.storage
//    private val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//        binding.signinButton.setOnClickListener {
//            if (binding.emailEditText.text.isBlank() || binding.nameEditText.text.isBlank() ||
//                binding.passwordEditText.text.isBlank() || binding.passwordConfirmEditText.text.isBlank()){
//                Toast.makeText(this,"빈칸이 남아있습니다. 빈칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.text).matches()){
//                Toast.makeText(this,"올바른 이메일 형태가 아닙니다.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            if (!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", binding.phoneEditText.text)){
//                Toast.makeText(this,"올바른 전화번호 형태가 아닙니다.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            if (binding.passwordConfirmEditText.text.toString() != binding.passwordEditText.text.toString()){
//                Toast.makeText(this,"비밀번호가 서로 동일 하지 않습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            Firebase.auth.createUserWithEmailAndPassword(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
//                .addOnCompleteListener {
//                    if (it.isSuccessful){
//                        Log.d("Sign in task","회원가입 성공");
//                        val user = Firebase.auth.currentUser
//                        val email = user!!.email
//                        val uid = user.uid
//                        val name = binding.nameEditText.text.toString()
//                        val phoneNumber = binding.phoneEditText.text.toString()
//                        val userFriendsList = mutableListOf<String>()
//                        val userdata = hashMapOf(
//                            "name" to name,
//                            "phone_number" to phoneNumber,
//                            "email" to email,
//                            "friends" to userFriendsList
//                        )
//                        userdataCollectionRef.document(uid).set(userdata).addOnFailureListener {
//                            Toast.makeText(this,"유저 데이터를 초기화하는데 실패했습니다.", Toast.LENGTH_SHORT).show()
//                        }
////                        val default = BitmapFactory.decodeResource(this.resources, R.mipmap.default_user_image)
////                        storage.reference.child("photo").child("${Firebase.auth.uid}.png").putBytes(bitmapToByteArray(default))
////                        finish()
//                    }
//                    else{
//                        Toast.makeText(this,"회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
    }
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
