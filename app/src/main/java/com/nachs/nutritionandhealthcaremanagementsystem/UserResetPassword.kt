package com.nachs.nutritionandhealthcaremanagementsystem

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_reset_password)
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickConfirmButton(view: View) {
        val email = findViewById<EditText>(R.id.etEmail).text.toString()
        val builder: CustomDialog = CustomDialog(this)
        // Verify email validity
        if (email.isEmpty()) {
            builder.setText("Please enter your email address.")
            builder.setCancellable(false)
            builder.show()
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            builder.setText("Please enter a valid email address.")
            builder.setCancellable(false)
            builder.show()
            return
        }
        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()
        // Send email
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                progressBarDialog.dismiss()
                if (task.isSuccessful) {
                    builder.setText("An email has been sent to $email. Please check your inbox.")
                    builder.setCancellable(false)
                    builder.setCallback { onBackPressed() }
                    builder.show()
                } else {
                    builder.setText("An error occurred. Please try again.")
                    builder.setCancellable(false)
                    builder.show()
                }
            }
    }
}