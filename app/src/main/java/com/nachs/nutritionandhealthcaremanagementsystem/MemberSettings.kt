package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class MemberSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_settings)
    }

    fun onClickNotifications(view: View) {
        val switch: SwitchCompat = findViewById(R.id.switchNotifications)
        switch.isChecked = !switch.isChecked
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickLogoutButton(view: View) {
        applicationContext.getSharedPreferences("prefs", 0).edit()
            .putBoolean("loggedIn", false).apply()
        val intent: Intent = Intent(this, UserTypeSelection::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}