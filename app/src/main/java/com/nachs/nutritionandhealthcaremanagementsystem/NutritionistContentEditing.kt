package com.nachs.nutritionandhealthcaremanagementsystem

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class NutritionistContentEditing : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutritionist_content_editing)
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onPostButtonClicked(view: View) {
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
        val auth = Firebase.auth
        val postsRef = db.collection("posts")
        postsRef.add(
            hashMapOf(
                "title" to postTitle.toString(),
                "content" to postContent.toString(),
                "postedAt" to Date().time,
                "postedBy" to auth.currentUser?.uid
            )
        )
        progressBarDialog.dismiss()
        val customDialog = CustomDialog(this)
        customDialog.setText("Post successfully added")
        customDialog.setCallback {
            onBackPressed()
        }
        customDialog.setCancellable(false)
        customDialog.show()
    }
}