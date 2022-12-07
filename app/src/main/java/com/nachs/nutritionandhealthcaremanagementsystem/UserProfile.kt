package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class UserProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickEditButton(view: View) {
        val intent: Intent = Intent(this, EditUserProfile::class.java)
        startActivity(intent)
    }
}