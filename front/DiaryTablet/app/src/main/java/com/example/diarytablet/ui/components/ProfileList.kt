package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.domain.dto.response.ProfileListResponse
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.ui.theme.PastelNavy

@Composable
fun ProfileList(
    modifier: Modifier = Modifier,
    profileList: ProfileListResponse,
    onCreateProfile: () -> Unit,
    onChooseProfile: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        profileList.profiles.forEach { profile ->
            ProfileItem(profile = profile, onChooseProfile)
        }
        Surface(
            modifier = Modifier
                .size(246.dp, 336.dp)
                .padding(8.dp),
            color = Color.Transparent,


        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
                    .clickable { onCreateProfile() }

            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_plus),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp), // 아이콘 크기 설정
                )
            }
        }
    }
}

@Composable
fun ProfileItem(profile: Profile, onChooseProfile: () -> Unit ) {
    Box(
        modifier = Modifier
            .size(246.dp, 336.dp)
            .padding(8.dp)
        .clickable { onChooseProfile() }
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
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent // Surface 투명하게 설정
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profile.img)
                        .transformations(CircleCropTransformation())
                        .build(),
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
