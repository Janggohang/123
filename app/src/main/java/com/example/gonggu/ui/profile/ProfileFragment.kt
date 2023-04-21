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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.gonggu.MainActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.FragmentProfileBinding
import com.example.gonggu.ui.location.LocationFragment
import com.example.gonggu.ui.post.PostFragment

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {
    private var binding: FragmentProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        val activity = activity as MainActivity
        val myPost = binding!!.myPost
        val myLocation = binding!!.myLocation
        val profileImage = binding!!.profileImage
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val context = view.context

        // 내가 쓴 글
        myPost.setOnClickListener {
            activity.replaceFragment(PostFragment())
        }
        // 내 위치 설정
        myLocation.setOnClickListener {
            when {
                checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                -> {
                    activity.replaceFragment(LocationFragment())
                }
                shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {

                }
                else -> {
                    requestPermissions(
                        arrayOf(ACCESS_FINE_LOCATION),
                        1001
                    )
                }
            }
        }

        // 프로필 이미지 설정
        profileImage.setOnClickListener {
            when {
                checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
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
