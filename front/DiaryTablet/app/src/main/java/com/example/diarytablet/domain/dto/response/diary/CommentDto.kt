package com.example.diarytablet.domain.dto.response.diary

import java.time.LocalDateTime


data class CommentDto(
    val comment: String,
    val createdAt: LocalDateTime
)

data class CommentList(
    val commentList : List<CommentDto>
)
