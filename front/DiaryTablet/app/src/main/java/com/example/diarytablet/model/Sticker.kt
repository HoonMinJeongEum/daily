package com.example.diarytablet.model

// 나중에 이미지 url로 받아올 때 이걸 사용할 예정 (지금은 이미지 주소가 아니라 그냥 res ID)
//data class Sticker(
//    val id: Int,
//    val img: String,  // 이미지 주소를 위한 URL
//    val price: Int
//)

data class Sticker(
    val id: Int,
    val imgRes: Int,  // 리소스 ID를 사용
    val price: Int
)