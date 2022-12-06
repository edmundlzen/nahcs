package com.nachs.nutritionandhealthcaremanagementsystem

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class NutritionistContentPosting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutritionist_content_posting)
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }
}