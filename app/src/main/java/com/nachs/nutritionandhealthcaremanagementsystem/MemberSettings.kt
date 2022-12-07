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

    fun onClickProfileSettings(view: View) {
        val intent: Intent = Intent(this, UserProfile::class.java)
        startActivity(intent)
    }

    fun onClickAppointmentsHistory(view: View) {
        val intent: Intent = Intent(this, AppointmentsHistoryReport::class.java)
        startActivity(intent)
    }

    fun onClickActiveAppointments(view: View) {
        val intent: Intent = Intent(this, ActiveAppointmentsReport::class.java)
        startActivity(intent)
    }

    fun onClickTnc(view: View) {
        val builder: CustomDialog = CustomDialog(this)
        builder.setText("You have to agree to the terms and conditions to use this app.")
        builder.setCancellable(false)
        builder.show()
    }

    fun onClickAbout(view: View) {
        val builder: CustomDialog = CustomDialog(this)
        builder.setText("This app is developed by __ and __ for the health and wellness of the people.")
        builder.setCancellable(false)
        builder.show()
    }
}