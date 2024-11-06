package com.example.diarytablet.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Coupon(
    val id: Int,
    val description: String,
    val price: String,
    @SerializedName("created_at") val createdAt: Date
)