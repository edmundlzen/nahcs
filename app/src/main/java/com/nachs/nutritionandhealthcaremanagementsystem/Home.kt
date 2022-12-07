package com.nachs.nutritionandhealthcaremanagementsystem

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
        val userType =
            applicationContext.getSharedPreferences("prefs", MODE_PRIVATE).getString("userType", "")
        val intent: Intent = if (userType == "nutritionist") {
            Intent(this, NutritionistSettings::class.java)
        } else {
            Intent(this, MemberSettings::class.java)
        }
        startActivity(intent)
    }
}