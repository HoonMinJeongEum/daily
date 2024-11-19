package com.example.diarytablet.domain.dto.request

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class WordRequestDto (
    @SerializedName("id")
    val id : Int,

    @SerializedName("image")
    val image: MultipartBody.Part
)