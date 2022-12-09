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
        val number = findViewById<EditText>(R.id.etNumber).text.toString()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()
        val tnc = findViewById<CheckBox>(R.id.cbTnc)
        val formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        if (
            name.isEmpty() or
            gender.isEmpty() or
            (formatter.parse(birthDate) == null) or
            email.isEmpty() or
            number.isEmpty() or
            password.isEmpty() or
            !tnc.isChecked
        ) {
            val builder = CustomDialog(this)
            builder.setText("Please fill all the fields and accept the terms and conditions.")
            builder.setCancellable(false)
            builder.setCallback {
                builder.dismiss()
            }
            builder.show()
            return
        }

        // Verify validity of email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            val builder = CustomDialog(this)
            builder.setText("Please enter a valid email address.")
            builder.setCancellable(false)
            builder.setCallback {
                builder.dismiss()
            }
            builder.show()
            return
        }

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
                        "number" to number,
                    )

                    db.collection("users").document(user!!.uid).set(data)
                        .addOnSuccessListener {
                            val builder = CustomDialog(this)
                            builder.setText("Congratulations on successfully creating an account.")
                            builder.setCancellable(false)
                            builder.setCallback {
                                val intent: Intent = Intent(this, Home::class.java)
                                startActivity(intent)
                            }
                            builder.show()
                        }
                        .addOnFailureListener {
                            val builder = CustomDialog(this)
                            builder.setText("An error occurred. Please try again.")
                            builder.setCancellable(false)
                            builder.setCallback {
                                builder.dismiss()
                            }
                            builder.show()
                        }
                } else {
                    val builder = CustomDialog(this)
                    builder.setText("An error occurred while creating your account. Please try again.")
                    builder.setCancellable(false)
                    builder.setCallback {
                        builder.dismiss()
                    }
                    builder.show()
                }
            }
    }
}