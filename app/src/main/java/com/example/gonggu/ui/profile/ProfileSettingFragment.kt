package com.example.gonggu.ui.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.gonggu.MainActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.FragmentProfileSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileSettingFragment : Fragment() {

    private var binding : FragmentProfileSettingBinding? = null
    private lateinit var mAuth : FirebaseAuth
    private lateinit var nameEdit : EditText
    private lateinit var phoneEdit : EditText
    private val db = Firebase.database
    private val usersRef = db.getReference("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileSettingBinding.inflate(inflater, container, false)
        nameEdit = binding!!.nameEditText // 이름
        phoneEdit = binding!!.phoneEditText // 전화번호
        val completeBtn = binding!!.completeBtn // 완료 버튼

        // FirebaseAuth와 Firebase Realtime Database 객체를 초기화합니다.
        mAuth = Firebase.auth

        val mActivity = activity as MainActivity
        // 바텀 네비게이션 사용
        mActivity.addNavigation()

        usersRef.child(mAuth.currentUser?.uid!!).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(UserData::class.java)
                    nameEdit.setText(user?.name)
                    phoneEdit.setText(user?.phonenumber)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        // 프로필 설정 완료
        completeBtn.setOnClickListener {
            changeMyInfo()
        }

        return binding!!.root
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
                        val user = snapshot.getValue(UserData::class.java)
                        user?.name = nameEdit.text.toString()
                        user?.phonenumber = phoneEdit.text.toString()

                        userRef.setValue(user)
                            .addOnSuccessListener {
//                                Toast.makeText(
//                                    requireContext(),
//                                    "프로필 정보가 변경되었습니다.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                            }
                            .addOnFailureListener { e->
//                                Toast.makeText(
//                                    requireContext(),
//                                    "프로필 정보 변경에 실패 했습니다: $e",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

        activity?.supportFragmentManager?.popBackStack()
    }

}

