package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.concurrent.Executors

class UserProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()

        val db = Firebase.firestore
        val auth = Firebase.auth
        val docRef = db.collection("users").document(auth.currentUser!!.uid)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString("name")
                    val email = document.getString("email")
                    val phone = document.getString("number")
                    val address = document.getString("address")
                    val gender = document.getString("gender")
                    val birthDate = document.getTimestamp("birthDate")
                    val profilePicture = document.getString("profilePicture")

                    findViewById<TextView>(R.id.tvName).text = name ?: "Not Set"
                    findViewById<TextView>(R.id.tvEmailContent).text = email ?: "Not Set"
                    findViewById<TextView>(R.id.tvContactNumber).text = phone ?: "Not Set"
                    findViewById<TextView>(R.id.tvAddressContent).text = address ?: "Not Set"
                    findViewById<TextView>(R.id.tvGenderContent).text = gender ?: "Not Set"
                    findViewById<TextView>(R.id.tvDateOfBirthContent).text =
                        if (birthDate != null) {
                            SimpleDateFormat("dd/MM/yyyy").format(birthDate.toDate())
                        } else {
                            "Not Set"
                        }

                    if (profilePicture != null) {
                        val executor = Executors.newSingleThreadExecutor()
                        executor.execute {
                            val bitmap = getBitmapFromURL(profilePicture)
                            runOnUiThread {
                                findViewById<ShapeableImageView>(R.id.ivProfilePicture)
                                    .setImageBitmap(bitmap)
                            }
                        }
                    } else {
                        findViewById<ShapeableImageView>(R.id.ivProfilePicture)
                            .setImageResource(R.drawable.profile_pic_test)
                    }

                    progressBarDialog.dismiss()
                } else {
                    progressBarDialog.dismiss()

                    val customDialog = CustomDialog(this)
                    customDialog.setText("An error occurred. Please try again later.")
                    customDialog.setCancellable(false)
                    customDialog.setCallback {
                        customDialog.dismiss()

                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                    }
                    customDialog.show()
                }
            }
            .addOnFailureListener {
                progressBarDialog.dismiss()

                val customDialog = CustomDialog(this)
                customDialog.setText("An error occurred. Please try again later.")
                customDialog.setCancellable(false)
                customDialog.setCallback {
                    customDialog.dismiss()

                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                }
                customDialog.show()
            }
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickEditButton(view: View) {
        val intent: Intent = Intent(this, EditUserProfile::class.java)
        startActivity(intent)
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}