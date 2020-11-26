package com.example.qatar_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.activity_welcome.motion

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        watchNameField()
        confirm.setOnClickListener {
            motion.transitionToState(R.id.collapsed)
        }

        linkImagesWithClick()


    }

    private fun linkImagesWithClick() {
        img4.setOnClickListener(this)
        img5.setOnClickListener(this)
        img6.setOnClickListener(this)
        img7.setOnClickListener(this)
        img8.setOnClickListener(this)
        img9.setOnClickListener(this)
        img10.setOnClickListener(this)
        img11.setOnClickListener(this)
        img12.setOnClickListener(this)
    }

    private fun watchNameField() {
        nameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("hello", "beforeTextChanged: ")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("hello", "beforeTextChanged: ")
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! > 4) {
                    confirm.isEnabled = true
                    confirm.setBackgroundResource(R.color.lovley_green)
                } else {
                    confirm.isEnabled = false
                    confirm.setBackgroundResource(R.color.gray)

                }
            }

        })
    }

    override fun onClick(v: View?) {
        unsetAllImagesBackgrounds()
        v?.setBackgroundResource(R.drawable.dashed_box_shape)
    }

    private fun unsetAllImagesBackgrounds() {
        img4.background = null
        img5.background = null
        img6.background = null
        img7.background = null
        img8.background = null
        img9.background = null
        img10.background = null
        img11.background = null
        img12.background = null
    }
}