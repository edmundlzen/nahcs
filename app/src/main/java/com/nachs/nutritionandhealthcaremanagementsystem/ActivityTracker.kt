package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class ActivityTracker : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()
        val executor = Executors.newSingleThreadExecutor()
        executor.submit(Runnable {
            lifecycleScope.launch {
                val activities = getActivities()
                runOnUiThread {
                    if (activities.size != 0) {
                        val tvNoActivities: Group = findViewById(R.id.groupNoActivities)
                        tvNoActivities.visibility = View.GONE
                    }
                    val recyclerView: RecyclerView = findViewById(R.id.rvActivities)
                    recyclerView.adapter = ActivitiesAdapter(activities)
                    progressBarDialog.dismiss()
                }
            }
        })
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickAddButton(view: View) {
        val intent = Intent(this, ActivityAdding::class.java)
        startActivity(intent)
    }

    private suspend fun getActivities(): ArrayList<ActivityTrackerActivity> =
        withContext(Dispatchers.Default) {
            val db = Firebase.firestore
            val auth = Firebase.auth
            val activitiesRef =
                db.collection("activities").whereEqualTo("user", auth.currentUser?.uid)
            val activities = ArrayList<ActivityTrackerActivity>()
            val activitiesData = activitiesRef.get().await()
            for (activity in activitiesData) {
                val activityId = activity.id
                val activityName = activity.getString("activityName")!!
                val activityTime = activity.getTimestamp("activityTime")
                val activity = ActivityTrackerActivity(
                    activityId,
                    activityName,
                    activityTime?.toDate()!!
                )
                activities.add(activity)
            }
            // Sort activities by time
            activities.sortBy { it.activityTime }

            return@withContext activities
        }
}