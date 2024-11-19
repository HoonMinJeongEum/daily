package com.example.diarytablet.domain.dto.response

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class WordLearnedResponseDto(
    @SerializedName("id")
    val id : Int,

    @SerializedName("word")
    val word : String,

    @SerializedName("img")
    val imageUrl : String,

    @SerializedName("org")
    val orgUrl : String,

    @SerializedName("createdAt")
    val createdAt : LocalDateTime

)
