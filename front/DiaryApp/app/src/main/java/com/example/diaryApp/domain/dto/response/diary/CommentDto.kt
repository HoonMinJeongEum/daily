package com.example.diaryApp.domain.dto.response.diary


data class CommentDto(
    val comment: String,
    val createdAt: String
)

data class CommentList(
    val commentList : List<CommentDto>
)