package com.example.diarytablet.domain.dto.response

import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("img")
    val img: String
)

data class ProfileListResponse(
    @SerializedName("profiles")
    val profiles: List<Profile>
)
