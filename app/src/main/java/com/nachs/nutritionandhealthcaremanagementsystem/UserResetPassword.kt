package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class UserResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_reset_password)
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickConfirmButton(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("An email has been sent")
        builder.setMessage("Please check the email you received to reset your password")
        builder.setPositiveButton("OK") { dialog, which ->
            val intent = Intent(this, UserLogin::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
            startActivity(intent, options.toBundle())
        }
        builder.show()
    }
}