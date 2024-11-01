package com.example.diaryApp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.White

@Composable
fun NavMenu() {
    val selectedMenu = remember { mutableStateOf("main") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = White,
                shape = RoundedCornerShape(topStart = 54.dp, topEnd = 54.dp)
            )
            .size(80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        val menuItems = listOf("main", "shop", "notification", "setting")
        val beforeImages = listOf(
            R.drawable.nav_before_main_icon,
            R.drawable.nav_before_shopping_icon,
            R.drawable.nav_before_notification_icon,
            R.drawable.nav_before_setting_icon
        )
        val afterImages = listOf(
            R.drawable.nav_after_main_icon,
            R.drawable.nav_after_shopping_icon,
            R.drawable.nav_after_notification_icon,
            R.drawable.nav_after_setting_icon
        )

        menuItems.forEachIndexed { index, menuItem ->
            val isSelected = selectedMenu.value == menuItem
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(
                        onClick = {
                            selectedMenu.value = menuItem
                        },
                        indication = null,
                        interactionSource = remember{ MutableInteractionSource() },
                    )
                    .background(
                        if (isSelected) DeepPastelNavy else Color.Transparent,
                        shape = androidx.compose.foundation.shape.CircleShape // 동그란 모양 설정
                    )
                    .size(60.dp),
                contentAlignment = Alignment.Center // 이미지를 중앙으로 정렬
            ) {
                Image(
                    painter = painterResource(id = if (isSelected) afterImages[index] else beforeImages[index]),
                    contentDescription = "$menuItem icon",
                    modifier = Modifier
                        .size(30.dp)
                )
            }
        }
    }
}
