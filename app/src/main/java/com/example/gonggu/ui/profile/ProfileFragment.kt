package com.example.gonggu.ui.profile

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gonggu.MainActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.FragmentProfileBinding
import com.example.gonggu.ui.chat.ChatData
import com.example.gonggu.ui.location.LocationFragment
import com.example.gonggu.ui.login.LoginActivity
import com.example.gonggu.ui.post.MyPostsFragment
import com.example.gonggu.ui.post.PostFragment
import com.example.gonggu.ui.post.PostViewerActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class UserData (val email:String, var name : String, var phonenumber : String, val uid: String){
    constructor(): this("","","","")
}
@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    private var binding: FragmentProfileBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = Firebase.auth
        mDatabase = Firebase.database.reference.child("user")
        val binding = FragmentProfileBinding.bind(view)

        mDatabase.child(mAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserData::class.java)
                    binding!!.nameTextView.text = user?.name
                    binding!!.phoneTextView.text = user?.phonenumber
                    binding!!.emailTextView.text = user?.email
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // FirebaseAuth와 Firebase Realtime Database 객체를 초기화합니다.
        mAuth = Firebase.auth
        mDatabase = Firebase.database.reference.child("user")

        val root: View = binding!!.root

        val activity = activity as MainActivity
        val myPost = binding!!.myPost
        val myLocation = binding!!.myLocation
        val settingBtn = binding!!.settingButton
        val profileImage = binding!!.profileImage
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val context = view.context



//        val spinner: Spinner = binding!!.spinner
//
//        val spinnerItems = resources.getStringArray(R.array.spinner_items)
//        val spinnerBackground = ContextCompat.getDrawable(requireContext(), R.drawable.dropdown)
//
//        spinner.background = spinnerBackground
//        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, spinnerItems)
//        spinner.adapter = spinnerAdapter
//
        // 내 정보 설정
        settingBtn.setOnClickListener {
            val intent = Intent(requireActivity(), OptionActivity::class.java)
            startActivity(intent)
        }

        // 내가 쓴 글
        myPost.setOnClickListener {
            activity.replaceFragment(MyPostsFragment())
        }
        // 내 위치 설정
        myLocation.setOnClickListener {
            activity.replaceFragment(LocationFragment())
        }

//        // 로그아웃
//        logout.setOnClickListener{
//            val intent = Intent(context, LoginActivity::class.java)
//            mAuth.signOut()
//            startActivity(intent)
//        }

        // 프로필 이미지 설정
        profileImage.setOnClickListener {
            when {
                checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED
                -> {
                    startContentProvider()
                }
                shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(READ_EXTERNAL_STORAGE),
                        1000)
                }
            }
        }

//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                when (position) {
//                    0 -> {
//                        // 개인정보 수정 기능 추가
//                        val intent = Intent(requireActivity(), ProfileSettingActivity::class.java)
//                        startActivity(intent)
//                    }
//                    2 -> {
//                        // 로그아웃 기능 추가
//                        val intent = Intent(context, LoginActivity::class.java)
//                        mAuth.signOut()
//                        startActivity(intent)
//                    }
//                }
//            }
//
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                // Do nothing
//            }
//        }
//
        return root
    }

    // 권한 요청 승인 이후 실행되는 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startContentProvider()
                else
                    Toast.makeText(view?.context,"권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                showPermissionContextPopup()
            }
            else -> {
                //
            }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 예외처리
        if (resultCode != Activity.RESULT_OK)
            return

        when (requestCode) {
            2020 -> {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    binding!!.profileImage.setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(view?.context, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(view?.context, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(view?.context)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
                    .setPositiveButton("확인") { _, _ ->
                        requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), 1010)
                    }
                    .create()
                    .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
