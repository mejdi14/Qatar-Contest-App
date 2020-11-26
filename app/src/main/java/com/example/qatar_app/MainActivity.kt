package com.example.qatar_app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import asm.asmtunis.com.bookswipe.fragment.FirstFragment
import asm.asmtunis.com.bookswipe.fragment.SecondFragment
import asm.asmtunis.com.bookswipe.fragment.ThirdFragment
import com.example.qatar_app.ui.fragment.MapFragment
import com.example.qatar_app.ui.fragment.MessagingFragment
import com.example.qatar_app.ui.fragment.MyMapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            //here goes static initializer code
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_patients -> showFragment(FirstFragment())
                    R.id.action_create -> showFragment(MyMapFragment())
                    R.id.action_calendar -> showFragment(MessagingFragment())
                    R.id.action_sync -> showFragment(SecondFragment())
                }
                false
            })
        showFragment(FirstFragment())

    }

    fun showFragment(fragment: Fragment?) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment!!)
        ft.commit()
    }
}