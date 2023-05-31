package com.example.gonggu

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import android.Manifest.permission.ACCESS_FINE_LOCATION

import android.content.pm.PackageManager
import android.os.Build
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

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}
    private lateinit var bottomNavigationView: BottomNavigationView

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Gonggu)
        current = this
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // CAUTION: findNavController(R.id.fragment) in onCreate will fail.
        bottomNavigationView = findViewById(R.id.bottomNav)
        bottomNavigationView.setupWithNavController(navController)

        val fragment = HomeFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment,fragment).commit()

        // 해시키 구하기
        try {
            val information =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = information.signingInfo.apkContentsSigners
            for (signature in signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                var hashcode = String(Base64.encode(md.digest(), 0))
                Log.d("hashcode", "" + hashcode)
            }
        } catch (e: Exception) {
            Log.d("hashcode", "에러::" + e.toString())

        }

    }

//    fun refresh(){
//        this.onResume()
//    }
    fun replaceFragment(fragment : Fragment) {
        Log.d("MainActivity","${fragment}")
        supportFragmentManager
            .beginTransaction()
            .apply {
                replace(R.id.fragment,fragment)
                    .addToBackStack(null)
                commit()
            }
    }
    fun addNavigation() { // navigation
        bottomNavigationView.setOnItemSelectedListener { item->
            when (item.itemId){
                R.id.navigation_homeFragment ->{
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_chatFragment -> {
                    replaceFragment(ChatFragment())
                    true
                }
                R.id.navigation_heartFragment -> {
                    replaceFragment(HeartFragment())
                    true
                }
                R.id.navigation_profileFragment -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }


    companion object{
        lateinit var current : MainActivity
    }
}