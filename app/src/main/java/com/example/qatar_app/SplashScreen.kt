package com.example.qatar_app

import android.content.Intent
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityOptionsCompat
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            startNewActivity()
        }, 4000)
    }

    private fun startNewActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
       /* val options = ActivityOptionsCompat.
        makeSceneTransitionAnimation(this, circle, "transition_image")*/
        startActivity(intent)
        finish()
    }

}