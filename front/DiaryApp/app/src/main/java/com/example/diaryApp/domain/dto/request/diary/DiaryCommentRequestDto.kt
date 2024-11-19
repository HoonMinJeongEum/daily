package com.example.diaryApp.domain.dto.request.diary

import com.google.gson.annotations.SerializedName

data class DiaryCommentRequestDto(
    @SerializedName("diaryId")
    val diaryId : Int,

    @SerializedName("comment")
    val comment : String
)
