package com.example.diarytablet.domain.dto.response.alarm

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class AlarmResponseDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("titleId")
    val titleId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("createdAt")
    val createdAt: LocalDateTime,
    @SerializedName("confirmedAt")
    val confirmedAt: LocalDateTime?
)
