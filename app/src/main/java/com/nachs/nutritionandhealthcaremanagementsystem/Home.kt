package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun onClickSettingsButton(view: View) {
        val userType = intent.getStringExtra("userType")
        val intent: Intent = Intent(this, MemberSettings::class.java)
        val options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this)
        startActivity(intent, options.toBundle())
    }
}