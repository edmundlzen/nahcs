package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class UserTypeSelection : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_type_selection)
    }

    fun onClickUser(view: View) {
        val logoView: View = findViewById<View>(R.id.ivLogo)
        val intent: Intent = Intent(this, UserOnboard::class.java)
        val options: ActivityOptions =
            ActivityOptions.makeSceneTransitionAnimation(this, logoView, "logo")
        startActivity(intent, options.toBundle())
    }

    fun onClickNutritionist(view: View) {
        val logoView: View = findViewById<View>(R.id.ivLogo)
        val intent: Intent = Intent(this, UserLogin::class.java)
        intent.putExtra("fromNutritionist", true)
        val options: ActivityOptions =
            ActivityOptions.makeSceneTransitionAnimation(this, logoView, "logo")
        startActivity(intent, options.toBundle())
    }
}