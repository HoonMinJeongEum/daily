package com.example.diarytablet.domain.dto.response.diary

import java.time.LocalDateTime

data class Diary(
    val id: Int,
    val drawImg: String,
    val writeImg: String,
    val sound: String,
    val video: String,
    val createdAt: LocalDateTime,
    val comments: List<CommentDto>
)

data class DiaryForList(
    val id : Int,
    val createdAt : LocalDateTime
)

data class DiaryList(
    val diaries: List<DiaryForList>
)
