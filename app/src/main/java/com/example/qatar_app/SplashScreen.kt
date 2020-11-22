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

    private fun startAnimation() {
        motion.transitionToState(R.id.collapsed)
        motion.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                Log.d("nice", "onTransitionStarted: ")

            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                Log.d("nice", "onTransitionStarted: ")
            }

            @RequiresApi(Build.VERSION_CODES.KITKAT)
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                Log.d("visibility switch", "onTransitionCompleted: ")
                intro.animate().alpha(1f).setDuration(500).setUpdateListener {
                    it.doOnEnd {
                        floating.animate().alpha(1f).setDuration(500)
                    }
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                Log.d("nice", "onTransitionStarted: ")
            }

        })
    }
}