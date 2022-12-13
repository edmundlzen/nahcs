package com.nachs.nutritionandhealthcaremanagementsystem

import android.graphics.Bitmap

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val authorName: String,
    val authorProfilePicture: Bitmap?,
    val postedAt: java.util.Date
)