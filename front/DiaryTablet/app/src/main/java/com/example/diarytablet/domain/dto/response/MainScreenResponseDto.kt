package com.example.diarytablet.domain.dto.response

import com.google.gson.annotations.SerializedName

data class MainScreenResponseDto (
    @SerializedName("img")
    val image : String,

    @SerializedName("diaryStatus")
    val diaryStatus : Boolean,

    @SerializedName("quizStatus")
    val quizStatus : Boolean,

    @SerializedName("wordStatus")
    val wordStatus : Boolean,

    @SerializedName("shellCount")
    val shellCount : Int
    )