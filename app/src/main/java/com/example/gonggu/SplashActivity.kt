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

import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils

import com.example.gonggu.databinding.ActivitySplashBinding
import com.example.gonggu.ui.login.LoginActivity


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        splashAnimation()
    }
    private fun splashAnimation() {
        val imageAnim = AnimationUtils.loadAnimation(this, R.anim.anim_splash_imageview)
        binding.splash.startAnimation(imageAnim)

        imageAnim.setAnimationListener(object : AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.anim_splash_out_top, R.anim.anim_splash_in_down)
                finish()
            }

            override fun onAnimationStart(animation: Animation?) {

            }


            override fun onAnimationRepeat(animation: Animation?) {
                TODO("Not yet implemented")
            }

        })
    }
}
