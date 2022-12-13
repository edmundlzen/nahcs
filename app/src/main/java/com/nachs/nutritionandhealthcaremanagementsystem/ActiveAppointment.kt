package com.nachs.nutritionandhealthcaremanagementsystem

import java.util.*

data class ActiveAppointment(
    val id: String,
    val date: Date,
    val time: String,
    val nutritionistName: String,
    val nutritionistId: String,
    val userName: String,
    val userId: String,
    val isHistory: Boolean = false
)