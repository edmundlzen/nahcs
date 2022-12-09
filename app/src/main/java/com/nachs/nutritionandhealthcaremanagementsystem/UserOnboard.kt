package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class UserOnboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_onboard)
    }

    fun onClickEmailSignInButton(view: View) {
        val logoView: View = findViewById<View>(R.id.ivLogo)
        val intent: Intent = Intent(this, UserLogin::class.java)
        val options: ActivityOptions =
            ActivityOptions.makeSceneTransitionAnimation(this, logoView, "logo")
        startActivity(intent, options.toBundle())
    }

    fun onClickUserSignUpButton(view: View) {
        val intent: Intent = Intent(this, UserSignup::class.java)
        val options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this)
        startActivity(intent, options.toBundle())
    }
}