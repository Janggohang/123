package com.example.gonggu.ui.profile

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.bumptech.glide.Glide
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

data class UserData (val email: String, var name: String, var phonenumber: String, val uid: String) {
    constructor() : this("", "", "", "")
}

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private var binding: FragmentProfileBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = Firebase.auth
        mDatabase = Firebase.database.reference.child("user")
        storage = Firebase.storage
        val binding = FragmentProfileBinding.bind(view)

        mDatabase.child(mAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserData::class.java)
                    binding!!.nameTextView.text = user?.name
                    binding!!.phoneTextView.text = user?.phonenumber
                    binding!!.emailTextView.text = user?.email
                    // 프로필 사진 로드
                    val profileImageRef = storage.reference.child("gonggu/userProfile/${mAuth.currentUser!!.uid}.png")
                    profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                        Glide.with(requireContext())
                            .load(uri)
                            .into(binding!!.profileImage)
                    }.addOnFailureListener {
                        // 프로필 사진 로드 실패 시 처리할 내용
                    }
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

        mAuth = Firebase.auth
        mDatabase = Firebase.database.reference.child("user")

        val root: View = binding!!.root

        val activity = activity as MainActivity
        val myPost = binding!!.myPost
        val myLocation = binding!!.myLocation
        val settingBtn = binding!!.settingButton
        val profileImage = binding!!.profileImage

        val context = root.context

        settingBtn.setOnClickListener {
            val intent = Intent(requireActivity(), OptionActivity::class.java)
            startActivity(intent)
        }

        myPost.setOnClickListener {
            activity.replaceFragment(MyPostsFragment())
        }

        myLocation.setOnClickListener {
            activity.replaceFragment(LocationFragment())
        }

        profileImage.setOnClickListener {
            if (checkStoragePermission()) {
                openImagePicker()
            } else {
                requestStoragePermission()
            }
        }

        return root
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = context?.let { ContextCompat.checkSelfPermission(it, READ_MEDIA_IMAGES) }
            result == PackageManager.PERMISSION_GRANTED
        } else {
            val result = PermissionChecker.checkSelfPermission(requireContext(), READ_MEDIA_IMAGES)
            result == PermissionChecker.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        requestPermissions(arrayOf(READ_MEDIA_IMAGES), 1000)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openImagePicker()
                else
                    Toast.makeText(view?.context, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                showPermissionContextPopup()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri? = data?.data
            selectedImage?.let { uri ->
                uploadPhoto(uri,
                    successHandler = { imageUrl ->
                        // Update the profile image in Firestore or any other necessary actions
                        val fileName = "${Firebase.auth.currentUser!!.uid}.png" // Update file name to use the UID
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .into(binding!!.profileImage)
                    },
                    errorHandler = {
                        Toast.makeText(requireContext(), "Failed to upload profile image", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }


    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${mAuth.currentUser!!.uid}.png" // Update file name to use the UID
        storage.reference.child("gonggu/userProfile").child(fileName)
            .putFile(uri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storage.reference.child("gonggu/userProfile").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }
                        .addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }


    private fun showPermissionContextPopup() {
        AlertDialog.Builder(view?.context)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("확인") { _, _ ->
                requestPermissions(arrayOf(READ_MEDIA_IMAGES), 1000)
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
