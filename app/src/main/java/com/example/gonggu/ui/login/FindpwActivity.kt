package com.example.gonggu.ui.login

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.ActivityForgetpasswordBinding
import com.google.firebase.auth.FirebaseAuth

class FindpwActivity: AppCompatActivity()  {
    val binding by lazy { ActivityForgetpasswordBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val email = findViewById<EditText>(R.id.id_edit_text)

        binding.loginButton.setOnClickListener{
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.text.toString())
        }
    }
}