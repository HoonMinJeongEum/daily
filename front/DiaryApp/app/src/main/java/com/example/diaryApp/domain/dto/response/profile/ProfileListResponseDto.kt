package com.example.diaryApp.domain.dto.response.profile

import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("img")
    val img: String,

    @SerializedName("shellCount")
    val shellCount: Int
)

data class ProfileListResponse(
    @SerializedName("profiles")
    val profiles: List<Profile>
)
