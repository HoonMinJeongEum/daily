package com.example.diaryApp.domain.dto.response.diary

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Diary(
    @SerializedName("id")
    val id: Int,
    @SerializedName("drawImg")
    val drawImg: String,
    @SerializedName("writeImg")
    val writeImg: String,
    @SerializedName("sound")
    val sound: String,
    @SerializedName("video")
    val video : String,
    @SerializedName("content")
    val content : String,
    @SerializedName("createdAt")
    val createdAt: LocalDateTime,
    @SerializedName("comments")
    val comments: List<CommentDto>

)

data class DiaryForList(
    val id : Int,
    val createdAt : LocalDateTime
)

data class DiaryList(
    val diaries: List<DiaryForList>
)