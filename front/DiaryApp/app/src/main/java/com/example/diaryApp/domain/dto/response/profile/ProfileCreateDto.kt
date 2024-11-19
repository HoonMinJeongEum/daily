package com.example.diaryApp.domain.dto.response.profile

import com.google.gson.annotations.SerializedName

data class ProfileCreateDto(
    @SerializedName("status")
    val status: Int,

    @SerializedName("msg")
    val msg: String,

)