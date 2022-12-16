package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class NutritionistContentPosting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutritionist_content_posting)
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onPostButtonClicked(view: View) {
        val postTitle = findViewById<EditText>(R.id.etPostTitle).text
        val postContent = findViewById<EditText>(R.id.etPostContent).text

        if (postTitle.isBlank() or postContent.isBlank()) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please fill in all the fields")
            customDialog.setCancellable(false)
            customDialog.show()
            return
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
                "postedAt" to Timestamp(Date()),
                "postedBy" to auth.currentUser?.uid
            )
        )
        progressBarDialog.dismiss()
        val customDialog = CustomDialog(this)
        customDialog.setText("Post successfully added")
        customDialog.setCallback {
            val intent = Intent(this, Home::class.java)
            val bundle = Bundle()
            intent.putExtras(bundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        customDialog.setCancellable(false)
        customDialog.show()
    }
}