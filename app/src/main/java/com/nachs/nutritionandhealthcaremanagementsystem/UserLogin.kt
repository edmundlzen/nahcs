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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserLogin : AppCompatActivity() {
    private var fromNutritionist: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
        findViewById<TextView>(R.id.tvForgotPassword).paintFlags = Paint.UNDERLINE_TEXT_FLAG

        fromNutritionist = intent.getBooleanExtra("fromNutritionist", false)
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
                    val db = Firebase.firestore
                    val auth = Firebase.auth

                    db.collection("users").document(auth.currentUser!!.uid).get()
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val isNutritionist = task.result!!.getBoolean("isNutritionist")

                                if (isNutritionist == true && !fromNutritionist) {
                                    progressBarDialog.dismiss()
                                    val customDialog = CustomDialog(this)
                                    customDialog.setText("You are a nutritionist. Please login from the nutritionist login page.")
                                    customDialog.setCancellable(false)
                                    customDialog.setCallback {
                                        auth.signOut()
                                        customDialog.dismiss()
                                    }
                                    customDialog.show()
                                    return@addOnCompleteListener
                                }
                                val editor =
                                    applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
                                        .edit()
                                editor.putBoolean("isNutritionist", isNutritionist!!)
                                editor.apply()
                                progressBarDialog.dismiss()
                                val intent: Intent = Intent(this, Home::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                progressBarDialog.dismiss()
                                val customDialog = CustomDialog(this)
                                customDialog.setText("An error occurred. Please try again.")
                                customDialog.setCancellable(false)
                                customDialog.setCallback {
                                    customDialog.dismiss()
                                }
                                customDialog.show()
                            }
                        }
                } else {
                    val error = task.exception?.message

                    if (error == "The password is invalid or the user does not have a password.") {
                        progressBarDialog.dismiss()
                        val customDialog = CustomDialog(this)
                        customDialog.setText("Invalid password.")
                        customDialog.setCancellable(false)
                        customDialog.setCallback {
                            customDialog.dismiss()
                        }
                        customDialog.show()
                    } else if (error == "There is no user record corresponding to this identifier. The user may have been deleted.") {
                        progressBarDialog.dismiss()
                        val customDialog = CustomDialog(this)
                        customDialog.setText("Invalid email.")
                        customDialog.setCancellable(false)
                        customDialog.setCallback {
                            customDialog.dismiss()
                        }
                        customDialog.show()
                    } else {
                        progressBarDialog.dismiss()
                        val customDialog = CustomDialog(this)
                        customDialog.setText("An error occurred. Please try again.")
                        customDialog.setCancellable(false)
                        customDialog.setCallback {
                            customDialog.dismiss()
                        }
                        customDialog.show()
                    }
                }
            }
    }
}