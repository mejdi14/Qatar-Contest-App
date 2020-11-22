package com.example.qatar_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.activity_welcome.motion

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        watchNameField()
        confirm.setOnClickListener {
            motion.transitionToState(R.id.collapsed)
        }


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
}