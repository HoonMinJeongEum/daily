package com.example.diaryApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopLogoImg(
                    characterImg = R.drawable.daily_character,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "자녀 관리",
                    color = Color.White,
                    style = MyTypography.bodyMedium,
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = 10.dp)
                )
            }

            // 가운데 흰색 박스 안에 DeleteProfileList 상단 정렬
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp))
                    .fillMaxWidth()
                    .height(780.dp) // 필요한 높이 지정
            ) {
                DeleteProfileList(
                    profileList = profileList,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp), // 원하는 만큼 상단 간격 설정
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
