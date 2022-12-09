package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class UserLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
        findViewById<TextView>(R.id.tvForgotPassword).paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickForgotPasswordButton(view: View) {
        val logoView: View = findViewById<View>(R.id.ivLogo)
        val intent: Intent = Intent(this, UserResetPassword::class.java)
        val options: ActivityOptions =
            ActivityOptions.makeSceneTransitionAnimation(this, logoView, "logo")
        startActivity(intent, options.toBundle())
    }

    fun onClickLoginButton(view: View) {
        val email = findViewById<EditText>(R.id.etEmail).text.toString()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()

        if (email.isEmpty() or password.isEmpty()) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please fill in all the fields.")
            customDialog.setCancellable(false)
            customDialog.setCallback {
                customDialog.dismiss()
            }
            customDialog.show()
            return
        }

        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()

        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressBarDialog.dismiss()
                    val intent: Intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    progressBarDialog.dismiss()
                    val customDialog = CustomDialog(this)
                    customDialog.setText("Invalid email or password.")
                    customDialog.setCancellable(false)
                    customDialog.setCallback {
                        customDialog.dismiss()
                    }
                    customDialog.show()
                }
            }
    }
}