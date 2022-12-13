package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NutritionistContentEditing : AppCompatActivity() {
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutritionist_content_editing)

        val bundle = intent.extras
        val postTitle = bundle?.getString("postTitle")
        val postContent = bundle?.getString("postContent")
        postId = bundle?.getString("postId")!!

        findViewById<EditText>(R.id.etPostTitle).setText(postTitle)
        findViewById<EditText>(R.id.etPostContent).setText(postContent)
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onSaveButtonClicked(view: View) {
        val postTitle = findViewById<EditText>(R.id.etPostTitle).text
        val postContent = findViewById<EditText>(R.id.etPostContent).text

        if (postTitle.isEmpty() or postContent.isEmpty()) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please fill in all the fields")
            customDialog.setCancellable(false)
            customDialog.show()
        }
        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()
        val db = Firebase.firestore
        val post = hashMapOf(
            "title" to postTitle.toString(),
            "content" to postContent.toString()
        )
        db.collection("posts").document(postId).update(post as Map<String, Any>)
            .addOnSuccessListener {
                progressBarDialog.dismiss()
                val customDialog = CustomDialog(this)
                customDialog.setText("Post successfully updated")
                customDialog.setCallback {
                    val intent = Intent(this, Home::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                customDialog.setCancellable(false)
                customDialog.show()
            }
            .addOnFailureListener {
                progressBarDialog.dismiss()
                val customDialog = CustomDialog(this)
                customDialog.setText("Something went wrong. Please try again later")
                customDialog.setCancellable(false)
                customDialog.show()
            }
    }
}