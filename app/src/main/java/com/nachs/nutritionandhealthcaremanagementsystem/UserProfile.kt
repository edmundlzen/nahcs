package com.nachs.nutritionandhealthcaremanagementsystem

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.canhub.cropper.*
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.concurrent.Executors


class UserProfile : AppCompatActivity() {
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val extras: Bundle? = result.data?.extras
                    if (extras != null) {
                        //Get image
                        val newProfilePic = extras.getParcelable<Bitmap>("data")
                        val profilePic: ShapeableImageView = findViewById(R.id.ivProfilePicture)
                        profilePic.setImageBitmap(newProfilePic)
                    }
                }
            }
        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()

        val db = Firebase.firestore
        val auth = Firebase.auth
        val docRef = db.collection("users").document(auth.currentUser!!.uid)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                val customDialog = CustomDialog(this)
                customDialog.setText("Error, please try again later")
                customDialog.setCancellable(false)
                customDialog.setCallback {
                    customDialog.dismiss()
                }
                customDialog.show()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val name = snapshot.getString("name")
                val phone = snapshot.getString("phone")
                val address = snapshot.getString("address")
                val gender = snapshot.getString("gender")
                val birthDate = snapshot.getTimestamp("birthDate")
                val profilePicture = snapshot.getString("profilePicture")

                findViewById<TextView>(R.id.tvName).text = name ?: "Not Set"
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

    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickEditButton(view: View) {
        val intent: Intent = Intent(this, EditUserProfile::class.java)
        val bundle = Bundle()
        bundle.putString(
            "name", if (findViewById<TextView>(R.id.tvName).text == "Not Set") {
                ""
            } else {
                findViewById<TextView>(R.id.tvName).text.toString()
            }
        )
        bundle.putString(
            "phone", if (findViewById<TextView>(R.id.tvContactNumber).text == "Not Set") {
                ""
            } else {
                findViewById<TextView>(R.id.tvContactNumber).text.toString()
            }
        )
        bundle.putString(
            "address", if (findViewById<TextView>(R.id.tvAddressContent).text == "Not Set") {
                ""
            } else {
                findViewById<TextView>(R.id.tvAddressContent).text.toString()
            }
        )
        bundle.putString(
            "gender", if (findViewById<TextView>(R.id.tvGenderContent).text == "Not Set") {
                ""
            } else {
                findViewById<TextView>(R.id.tvGenderContent).text.toString()
            }
        )
        bundle.putString(
            "birthDate", if (findViewById<TextView>(R.id.tvDateOfBirthContent).text == "Not Set") {
                null
            } else {
                findViewById<TextView>(R.id.tvDateOfBirthContent).text.toString()
            }
        )

        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun getBitmapFromURL(src: String?): Bitmap? {
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

    fun onClickProfilePicture(view: View) {
        pickImage()
    }

    private val customCropImage = registerForActivityResult(CropImageContract()) { cropResult ->
        if (cropResult !is CropImage.CancelledResult) {
            val progressBarDialog = ProgressBarDialog(this)
            progressBarDialog.show()

            val uri = cropResult.uriContent!!
            val storage = Firebase.storage
            val auth = Firebase.auth
            val storageRef = storage.reference
            val imageRef = storageRef.child("profilePictures/${auth.currentUser?.uid}.jpg")
            val uploadTask = imageRef.putFile(uri)
            uploadTask.addOnFailureListener {
                progressBarDialog.dismiss()

            }.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    val db = Firebase.firestore
                    db.collection("users").document(auth.currentUser?.uid.toString())
                        .update("profilePicture", it.toString())
                        .addOnSuccessListener {
                            val imageBitmap = uriToBitmap(uri)
                            findViewById<ShapeableImageView>(R.id.ivProfilePicture)
                                .setImageBitmap(imageBitmap)
                            progressBarDialog.dismiss()
                            val customDialog = CustomDialog(this)
                            customDialog.setText("Profile picture updated successfully.")
                            customDialog.setCancellable(false)
                            customDialog.show()
                        }
                        .addOnFailureListener {
                            progressBarDialog.dismiss()
                            val customDialog = CustomDialog(this)
                            customDialog.setText("An error occurred. Please try again later.")
                            customDialog.setCancellable(false)
                            customDialog.show()
                        }
                }
            }
        }
    }

    private fun pickImage() {
        if (ActivityCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                return
            }
            val uri = data?.data
            if (uri != null) {
                customCropImage.launch(
                    CropImageContractOptions(
                        uri,
                        cropImageOptions = CropImageOptions(
                            aspectRatioX = 1,
                            aspectRatioY = 1,
                            cropShape = CropImageView.CropShape.OVAL,
                            showCropOverlay = true,
                            fixAspectRatio = true,
                        )
                    )
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // pick image after request permission success
                    pickImage()
                }
            }
        }
    }

    private fun uriToImageFile(uri: Uri): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val filePath = cursor.getString(columnIndex)
                cursor.close()
                return File(filePath)
            }
            cursor.close()
        }
        return null
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        return MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
    }

    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 1000
        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1001
    }
}