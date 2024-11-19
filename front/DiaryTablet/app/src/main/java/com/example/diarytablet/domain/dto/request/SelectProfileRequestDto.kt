package com.example.diarytablet.domain.dto.request


import com.google.gson.annotations.SerializedName

data class SelectProfileRequestDto(
    @SerializedName("memberId")
    val memberId: Int,

)