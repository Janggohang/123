package com.example.gonggu

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent

import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.PersistableBundle
import android.util.Base64
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gonggu.databinding.ActivityMainBinding
import com.example.gonggu.ui.chat.ChatFragment
import com.example.gonggu.ui.heart.HeartFragment
import com.example.gonggu.ui.home.HomeFragment
import com.example.gonggu.ui.post.DeliveryPostFragment
import com.example.gonggu.ui.post.PostFragment
import com.example.gonggu.ui.profile.ProfileFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.splash_base)

        Handler().postDelayed({
            // Splash 화면이 표시된 후 수행할 작업을 여기에 작성합니다.
            // 예: 다음 화면으로 이동하는 Intent 등
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000) // 3초(3000 밀리초) 딜레이 설정
    }
}
