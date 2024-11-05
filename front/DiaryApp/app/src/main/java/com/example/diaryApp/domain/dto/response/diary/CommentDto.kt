package com.example.diaryApp.domain.dto.response.diary

import org.w3c.dom.Comment

data class CommentDto(
    val comment: String,
    val createdAt: String
)

data class CommentList(
    val commentList : List<Comment>
)