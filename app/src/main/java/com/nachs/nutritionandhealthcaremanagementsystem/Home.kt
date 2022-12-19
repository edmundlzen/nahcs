package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
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

        val isNutritionist =
            applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("isNutritionist", false)

        if (isNutritionist) {
            findViewById<View>(R.id.llMakeAppointment).visibility = View.GONE
        }

        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()

        val executor = Executors.newSingleThreadExecutor()
        executor.submit(Runnable {
            lifecycleScope.launch {
                refreshAppointmentNotifications(applicationContext)
                val posts = getPosts()
                runOnUiThread {
                    val recyclerView: RecyclerView = findViewById(R.id.rvPosts)
                    recyclerView.adapter = PostsAdapter(posts)
                    progressBarDialog.dismiss()
                }
            }
        })

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // Set a water notifications alarm that runs every hour
        val intent = Intent("com.nachs.nutritionandhealthcaremanagementsystem.NOTIFICATION")
        intent.putExtra("NOTIFICATION_TITLE", "Water Reminder")
        intent.putExtra(
            "NOTIFICATION_TEXT", "Drink some water! It's good for you!"
        )
        intent.putExtra("NOTIFICATION_ID", 1)
        intent.putExtra("IS_WATER_REMINDER", true)
        intent.component = ComponentName(
            "com.nachs.nutritionandhealthcaremanagementsystem",
            "com.nachs.nutritionandhealthcaremanagementsystem.NotificationReceiver"
        )
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            1000 * 60 * 60,
            pendingIntent
        )

        // Set an exercise notifications alarm that runs every 6 hours
        val intent2 = Intent("com.nachs.nutritionandhealthcaremanagementsystem.NOTIFICATION")
        intent2.putExtra("NOTIFICATION_TITLE", "Exercise Reminder")
        intent2.putExtra(
            "NOTIFICATION_TEXT", "Go for a walk! It's good for you!"
        )
        intent2.putExtra("NOTIFICATION_ID", 2)
        intent2.putExtra("IS_EXERCISE_REMINDER", true)
        intent2.component = ComponentName(
            "com.nachs.nutritionandhealthcaremanagementsystem",
            "com.nachs.nutritionandhealthcaremanagementsystem.NotificationReceiver"
        )
        val pendingIntent2 = PendingIntent.getBroadcast(
            applicationContext,
            2,
            intent2,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            1000 * 60 * 60 * 6,
            pendingIntent2
        )
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
                post.id,
                post.getString("title")!!,
                post.getString("content")!!,
                user.getString("name")!!,
                userProfilePicture,
                post.getDate("postedAt")!!,
                userId
            )
            posts.add(post)
        }
        // Sort posts by date
        posts.sortByDescending { it.postedAt }
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

    fun onClickCaloriesCalculator(view: View) {
        val intent = Intent(this, CaloriesCalculation::class.java)
        startActivity(intent)
    }

    fun onClickStepsCounter(view: View) {
        val intent = Intent(this, StepsCounter::class.java)
        startActivity(intent)
    }

    fun onClickActivityTracker(view: View) {
        val intent = Intent(this, ActivityTracker::class.java)
        startActivity(intent)
    }
}