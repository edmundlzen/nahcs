package com.nachs.nutritionandhealthcaremanagementsystem

import DatePickerFragment
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class UserSignup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_signup)

        val arraySpinner: Array<String> = arrayOf("Male", "Female")
        val s: Spinner = findViewById(R.id.spnGender)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arraySpinner)
        s.adapter = adapter
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickSelectDateButton(view: View) {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(supportFragmentManager, "datePicker")
        supportFragmentManager.setFragmentResultListener("datePicker", this) { key, bundle ->
            val date = bundle.getString("date")
            val textView: TextView = findViewById(R.id.btnBirthDate)
            textView.text = date
        }
    }

    fun onClickBackToLoginButton(view: View) {
        onBackPressed()
    }

    fun onClickSignupButton(view: View) {
        val name = findViewById<EditText>(R.id.etName).text.toString()
        val gender = findViewById<Spinner>(R.id.spnGender).selectedItem.toString()
        val birthDate = findViewById<AppCompatButton>(R.id.btnBirthDate).text.toString()
        val email = findViewById<EditText>(R.id.etEmail).text.toString()
        val phone = findViewById<EditText>(R.id.etNumber).text.toString()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()
        val tnc = findViewById<CheckBox>(R.id.cbTnc)
        val formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        if (
            name.isEmpty() or
            gender.isEmpty() or
            (formatter.parse(birthDate) == null) or
            email.isEmpty() or
            phone.isEmpty() or
            password.isEmpty() or
            !tnc.isChecked
        ) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please fill all the fields and accept the terms and conditions.")
            customDialog.setCancellable(false)
            customDialog.setCallback {
                customDialog.dismiss()
            }
            customDialog.show()
            return
        }

        // Verify validity of email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please enter a valid email address.")
            customDialog.setCancellable(false)
            customDialog.setCallback {
                customDialog.dismiss()
            }
            customDialog.show()
            return
        }

        // Check if password is at least 8 characters long, contains at least one uppercase letter, one lowercase letter and one digit
        if (!password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$"))) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter and one digit.")
            customDialog.setCancellable(false)
            customDialog.setCallback {
                customDialog.dismiss()
            }
            customDialog.show()
            return
        }

        if (phone.length > 11 || phone.length < 10) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please enter a valid phone number")
            customDialog.setCancellable(false)
            customDialog.setCallback {
                customDialog.dismiss()
            }
            customDialog.show()
            return
        }

        if (formatter.parse(birthDate).after(SimpleDateFormat("dd/MM/yyyy").parse("01/01/2007"))) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please enter a valid birth date before 2007")
            customDialog.setCancellable(false)
            customDialog.setCallback {
                customDialog.dismiss()
            }
            customDialog.show()
            return
        }

        if (!name.matches(Regex("[a-zA-Z ]+"))) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please enter a valid name without numbers")
            customDialog.setCancellable(false)
            customDialog.setCallback {
                customDialog.dismiss()
            }
            customDialog.show()
            return
        }
        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()

        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val db = Firebase.firestore

                    val data = hashMapOf(
                        "name" to name,
                        "gender" to gender,
                        "birthDate" to Timestamp(formatter.parse(birthDate) as Date),
                        "email" to email,
                        "phone" to phone,
                        "address" to null,
                        "profilePicture" to null,
                        "isNutritionist" to false,
                    )

                    db.collection("users").document(user!!.uid).set(data)
                        .addOnSuccessListener {
                            progressBarDialog.dismiss()
                            val editor =
                                applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
                                    .edit()
                            editor.putBoolean("isNutritionist", false)
                            editor.apply()
                            val customDialog = CustomDialog(this)
                            customDialog.setText("Congratulations on successfully creating an account.")
                            customDialog.setCancellable(false)
                            customDialog.setCallback {
                                val intent: Intent = Intent(this, Home::class.java)
                                startActivity(intent)
                            }
                            customDialog.show()
                        }
                        .addOnFailureListener {
                            progressBarDialog.dismiss()
                            val customDialog = CustomDialog(this)
                            customDialog.setText("An error occurred. Please try again.")
                            customDialog.setCancellable(false)
                            customDialog.setCallback {
                                customDialog.dismiss()
                            }
                            customDialog.show()
                        }
                } else {
                    progressBarDialog.dismiss()
                    val customDialog = CustomDialog(this)
                    customDialog.setText("An error occurred while creating your account. Please try again.")
                    customDialog.setCancellable(false)
                    customDialog.setCallback {
                        customDialog.dismiss()
                    }
                    customDialog.show()
                }
            }
    }
}