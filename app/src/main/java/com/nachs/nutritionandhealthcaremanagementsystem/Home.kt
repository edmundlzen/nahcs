package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()

        val executor = Executors.newSingleThreadExecutor()
        executor.submit(Runnable {
            lifecycleScope.launch {
                val posts = getPosts()
                runOnUiThread {
                    val recyclerView: RecyclerView = findViewById(R.id.rvPosts)
                    recyclerView.adapter = PostsAdapter(posts)
                    progressBarDialog.dismiss()
                }
            }
        })
    }

    private suspend fun getPosts(): ArrayList<Post> = withContext(Dispatchers.Default) {
        val db = Firebase.firestore
        val postsRef = db.collection("posts")
        val posts = ArrayList<Post>()
        val postsData = postsRef.get().await()
        for (post in postsData) {
            val userId = post.getString("postedBy")!!
            val user = db.collection("users").document(userId).get().await()
            val userProfilePicture: Bitmap? = if (user.getString("profilePicture") != null) {
                val url = URL(user.getString("profilePicture"))
                val inputStream: InputStream = url.openConnection().getInputStream()
                BitmapFactory.decodeStream(inputStream)
            } else {
                null
            }
            val post = Post(
                post.getString("title")!!,
                post.getString("content")!!,
                user.getString("name")!!,
                userProfilePicture,
                post.getDate("postedAt")!!
            )
            posts.add(post)
        }
        return@withContext posts
    }

    fun onClickSettingsButton(view: View) {
        val isNutritionist =
            applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("isNutritionist", false)
        val intent: Intent = if (isNutritionist) {
            Intent(this, NutritionistSettings::class.java)
        } else {
            Intent(this, MemberSettings::class.java)
        }
        startActivity(intent)
    }

    fun onClickBMICalculatorButton(view: View) {
        val intent = Intent(this, BMICalculation::class.java)
        startActivity(intent)
    }

    fun onClickMakeAppointment(view: View) {
        val intent = Intent(this, AppointmentBooking::class.java)
        startActivity(intent)
    }
}