package com.example.diaryApp.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.diaryApp.R
import com.example.diaryApp.domain.dto.response.Profile
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.PastelNavy
import com.example.diaryApp.viewmodel.ProfileViewModel
import java.io.File

@Composable
fun ProfileList(
    modifier: Modifier = Modifier,
    profileList : List<Profile>,
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    var showDialog by remember { mutableStateOf(false) }
    Log.d("ProfileScreen", "${profileList}")

    Column (
        modifier = modifier
            .fillMaxHeight()
            .wrapContentWidth()
    ) {
        profileList.forEach { profile ->
            ProfileItem(profile)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showDialog) {
            CreateProfile(
                profileViewModel = profileViewModel,
                onCancel = {
                    showDialog = false
                }
            )
        }  else {
            AddProfileButton(onClick = {showDialog = true})
        }
    }
}

@Composable
fun ProfileItem(profile: Profile) {
    Box(
        modifier = Modifier
            .size(246.dp, 336.dp)
            .padding(8.dp)
    ) {
        // 프로필 전체 배경
        Image(
            painter = painterResource(id = R.drawable.profile_container),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // 프로필 이미지와 이름을 세로로 정렬
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 프로필 이미지
            Surface(
                modifier = Modifier.size(160.dp),
                shape = RoundedCornerShape(48.dp),
                color = Color.Transparent // Surface 투명하게 설정
            ) {
//                AsyncImage(
//                    model = ImageRequest.Builder(LocalContext.current)
//                        .data(profile.img)
//                        .transformations(CircleCropTransformation())
//                        .build(),
//                    contentDescription = null, // 필요에 따라 이미지 설명 추가
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
                Image(
                    painter = painterResource(id = R.drawable.id),
                    contentDescription = null, // 필요에 따라 이미지 설명 추가
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // 이미지와 이름 사이 간격
            Text(
                text = profile.name,
                style = MyTypography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = PastelNavy
            )
        }
    }
}