package com.example.gonggu.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gonggu.MainActivity
import com.example.gonggu.databinding.ActivityProfileSettingBinding
import com.example.gonggu.databinding.ActivitySignupBinding
import com.example.gonggu.ui.login.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class ProfileSettingActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileSettingBinding
    private lateinit var nameEdit : EditText
    private lateinit var phoneEdit : EditText

    private val db = Firebase.database
    private val usersRef = db.getReference("user")

    lateinit var  mAuth: FirebaseAuth

    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화
        mAuth = Firebase.auth

        //인증 초기화
        mDbRef = Firebase.database.reference

        nameEdit = binding!!.nameEditText
        phoneEdit = binding!!.phoneEditText

        // 전화번호 입력 시 자동 하이픈 입력
        phoneEdit.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        val completeBtn = binding!!.completeBtn

        usersRef.child(mAuth.currentUser?.uid!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(com.example.gonggu.ui.profile.UserData::class.java)
                    nameEdit.setText(user?.name)
                    phoneEdit.setText(user?.phonenumber)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        // 프로필 설정 완료
        completeBtn.setOnClickListener {
            if (!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", binding!!.phoneEditText.text)){
                Toast.makeText(this,"올바른 전화번호 형태가 아닙니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            changeMyInfo()
        }
    }

    private fun changeMyInfo() {
        val user = mAuth.currentUser
        val userId = user?.uid

        // user 정보 변경
        if (userId != null) {
            val userRef = usersRef.child(userId)

            userRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(com.example.gonggu.ui.profile.UserData::class.java)
                        user?.name = nameEdit.text.toString()
                        user?.phonenumber = phoneEdit.text.toString()
                        
                        userRef.setValue(user)
                            .addOnSuccessListener {
                                val intent: Intent = Intent(this@ProfileSettingActivity, MainActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(
                                    this@ProfileSettingActivity,
                                    "프로필 정보가 변경되었습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e->
                                Toast.makeText(
                                    this@ProfileSettingActivity,
                                    "프로필 정보 변경에 실패 했습니다: $e",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }
}

