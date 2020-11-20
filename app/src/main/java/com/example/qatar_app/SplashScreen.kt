package com.example.qatar_app

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            startAnimation()
        }, 4000)
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

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                Log.d("visibility switch", "onTransitionCompleted: ")
                intro.animate().alpha(1f).setDuration(500)
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                Log.d("nice", "onTransitionStarted: ")
            }

        })
    }
}