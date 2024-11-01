package com.example.diaryApp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun BackgroundPlacement(backgroundType: BackgroundType) {
    val backgroundRes = backgroundType.getBackgroundResource()

    Image(
        painter = painterResource(id = backgroundRes),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}