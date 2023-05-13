package com.example.gonggu.ui.post

import com.example.gonggu.R
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FloatingBtnActivity : AppCompatActivity(), View.OnClickListener {
    private var fab_open: Animation? = null
    private var fab_close: Animation? = null
    private var isFabOpen = false
    private var fab: FloatingActionButton? = null
    private var fab1: FloatingActionButton? = null
    private var fab2: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fab_for_write)

        fab_open = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
        fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab1 = findViewById<View>(R.id.fab1) as FloatingActionButton
        fab2 = findViewById<View>(R.id.fab2) as FloatingActionButton
        fab!!.setOnClickListener(this)
        fab1!!.setOnClickListener(this)
        fab2!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.fab -> {
                anim()
                Toast.makeText(this, "Floating Action Button", Toast.LENGTH_SHORT).show()
            }

            R.id.fab1 -> {
                anim()
                Toast.makeText(this, "Button1", Toast.LENGTH_SHORT).show()
            }

            R.id.fab2 -> {
                anim()
                Toast.makeText(this, "Button2", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun anim() {
        if (isFabOpen) {
            fab1!!.startAnimation(fab_close)
            fab2!!.startAnimation(fab_close)
            fab1!!.isClickable = false
            fab2!!.isClickable = false
            isFabOpen = false
        } else {
            fab1!!.startAnimation(fab_open)
            fab2!!.startAnimation(fab_open)
            fab1!!.isClickable = true
            fab2!!.isClickable = true
            isFabOpen = true
        }
    }
}