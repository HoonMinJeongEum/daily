package com.example.diaryApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.ui.components.TopLogoImg
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.R
import com.example.diaryApp.ui.components.DeleteProfileList
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.ProfileList
import com.example.diaryApp.ui.components.TabletHeader
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.viewmodel.ProfileViewModel

@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.NORMAL,
) {
    BackgroundPlacement(backgroundType = backgroundType)

    val profileList by viewModel.profileList

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val footerHeight = screenWidth / 4.5f
        val textFieldHeight = screenWidth / 5f
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabletHeader(
                    pageName = "관리",
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                )
            }

            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp))
                    .fillMaxSize()
                    .padding(bottom = footerHeight)
            ){
                DeleteProfileList(
                    profileList = profileList,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = screenHeight * 0.04f), // 원하는 만큼 상단 간격 설정
                    viewModel = viewModel
                )
            }
        }

        // 하단 NavMenu를 항상 화면의 최하단에 고정
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            NavMenu(navController, "setting", "setting")
        }
    }
}
