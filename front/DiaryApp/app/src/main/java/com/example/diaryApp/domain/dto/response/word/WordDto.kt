package com.example.diaryApp.domain.dto.response.word

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Word (
    @SerializedName("id")
    val id : Int,

    @SerializedName("word")
    val word : String,

    @SerializedName("img")
    val url : String,

    @SerializedName("org")
    val org : String,

    @SerializedName("createdAt")
    val createdAt : LocalDateTime,
)

data class WordList (
    @SerializedName("WordList")
    val words : List<Word>
)