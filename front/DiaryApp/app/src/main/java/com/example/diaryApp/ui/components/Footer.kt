package com.example.diaryApp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.NavWhite

@Composable
fun NavMenu(
    navController: NavController,
    nowPage: String,
    destination: String?,
) {
    val selectedMenu = rememberSaveable { mutableStateOf(nowPage) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(currentBackStackEntry) {
        val currentRoute = currentBackStackEntry?.destination?.route
        if (currentRoute in listOf("diary", "word", "catchMind")) {
            selectedMenu.value = "main"
        } else if (currentRoute in listOf("main", "shop", "notification", "setting")) {
            if (currentRoute != null) {
                selectedMenu.value = currentRoute
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = NavWhite,
                shape = RoundedCornerShape(topStart = 54.dp, topEnd = 54.dp)
            )
            .aspectRatio(4.5f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        val menuItems = listOf("main", "shop", "notification", "setting")
        val destinations = listOf("main", "shop", "notification", "setting")
        val beforeImages = listOf(
            R.drawable.nav_before_main_icon,
            R.drawable.nav_before_shopping_icon,
            R.drawable.nav_before_notification_icon,
            R.drawable.nav_before_setting_icon,
        )
        val afterImages = listOf(
            R.drawable.nav_after_main_icon,
            R.drawable.nav_after_shopping_icon,
            R.drawable.nav_after_notification_icon,
            R.drawable.nav_after_setting_icon,
        )
        menuItems.forEachIndexed { index, menuItem ->
            val isSelected = selectedMenu.value == menuItem
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .aspectRatio(1f)
                    .clickable(
                        onClick = {
                            selectedMenu.value = menuItem
                            if (destination !in listOf("diary", "word", "catchMind")) {
                                navController.navigate(destinations[index]) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    )
                    .background(
                        if (isSelected) DeepPastelNavy else Color.Transparent,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = if (isSelected) afterImages[index] else beforeImages[index]),
                    contentDescription = "$menuItem icon",
                    modifier = Modifier.fillMaxSize(0.5f)
                )
            }
        }
    }
}
