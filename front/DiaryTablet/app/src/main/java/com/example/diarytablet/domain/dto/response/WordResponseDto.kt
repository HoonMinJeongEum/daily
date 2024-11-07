package com.example.diarytablet.domain.dto.response

import com.google.gson.annotations.SerializedName

data class WordResponseDto(
    @SerializedName("id")
    val id : Int,

    @SerializedName("word")
    val word : String,

    @SerializedName("img")
    val imageUrl : String,
)

data class WordStatusDto(
    @SerializedName("status")
    val status : Int,

    @SerializedName("msg")
    val msg : String
)