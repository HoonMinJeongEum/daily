package com.example.diaryApp.domain.dto.response.diary

import org.w3c.dom.Comment
import java.time.LocalDateTime

data class Diary(
    val id: Int,
    val img: String,
    val sound: String,
    val createdAt: LocalDateTime,
    val comments: List<Comment>
)

data class DiaryForList(
    val id : Int,
    val createdAt : LocalDateTime
)

data class DiaryList(
    val diaries: List<DiaryForList>
)