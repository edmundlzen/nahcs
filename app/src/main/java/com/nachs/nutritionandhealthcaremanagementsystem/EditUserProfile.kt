package com.nachs.nutritionandhealthcaremanagementsystem

import DatePickerFragment
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class EditUserProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val arraySpinner: Array<String> = arrayOf("Male", "Female")
        val s: Spinner = findViewById(R.id.spnGender)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arraySpinner)
        s.adapter = adapter

        val bundle = intent.extras
        val name = bundle?.getString("name")
        val phone = bundle?.getString("phone")
        val birthDate = bundle?.getString("birthDate")
        val address = bundle?.getString("address")
        val gender = bundle?.getString("gender")

        findViewById<EditText>(R.id.etName).setText(name)
        findViewById<EditText>(R.id.etNumber).setText(phone)
        findViewById<EditText>(R.id.etAddress).setText(address)
        findViewById<Button>(R.id.btnBirthDate).text = birthDate
        findViewById<Spinner>(R.id.spnGender).setSelection(if (gender == "Male") 0 else 1)
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

    fun onClickSaveButton(view: View) {
        val name = findViewById<EditText>(R.id.etName).text.toString()
        val gender = findViewById<Spinner>(R.id.spnGender).selectedItem.toString()
        val birthDate =
            SimpleDateFormat("dd/MM/yyyy").parse(findViewById<Button>(R.id.btnBirthDate).text.toString())
        val phone = findViewById<EditText>(R.id.etNumber).text.toString()
        val address = findViewById<EditText>(R.id.etAddress).text.toString()

        if (
            name.isEmpty() or
            gender.isEmpty() or
            (birthDate == null) or
            phone.isEmpty() or
            address.isEmpty()
        ) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please fill in all the fields")
            customDialog.setCancellable(false)
            customDialog.setCallback {
                customDialog.dismiss()
            }
            customDialog.show()
            return
        }

        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()

        val db = Firebase.firestore
        val auth = Firebase.auth
        val updatedUser = hashMapOf(
            "name" to name,
            "gender" to gender,
            "birthDate" to birthDate,
            "phone" to phone,
            "address" to address
        )

        db.collection("users").document(auth.currentUser!!.uid)
            .set(updatedUser, SetOptions.merge())
            .addOnSuccessListener {
                progressBarDialog.dismiss()
                onBackPressed()
            }
            .addOnFailureListener {
                progressBarDialog.dismiss()
                val customDialog = CustomDialog(this)
                customDialog.setText("Failed to update user profile")
                customDialog.setCancellable(false)
                customDialog.setCallback {
                    customDialog.dismiss()
                }
                customDialog.show()
            }

    }

    fun onClickCancelButton(view: View) {
        onBackPressed()
    }
}