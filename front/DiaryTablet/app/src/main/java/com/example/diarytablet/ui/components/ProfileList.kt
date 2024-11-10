package com.example.diarytablet.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.ui.theme.PastelNavy

@Composable
fun ProfileList(
    modifier: Modifier = Modifier,
    profileList: List<Profile>,
    onChooseProfile: (Profile) -> Unit,
) {
    Log.d("ProfileScreen","${profileList}")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),

        horizontalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.height(170.dp))

        profileList.forEach { profile ->
            // img가 null이거나 빈 문자열일 경우 기본 이미지를 설정
            val profileWithDefaultImg = if (profile.img.isNullOrEmpty()) {
                profile.copy(img = "https://example.com/default-image.jpg") // 기본 이미지 URL 또는 리소스 ID 사용
            } else {
                profile
            }
            ProfileItem(profile = profileWithDefaultImg, onChooseProfile = onChooseProfile)
        }

    }

}

@Composable
fun ProfileItem(profile: Profile, onChooseProfile: (Profile) -> Unit ) {
    Box(
        modifier = Modifier
            .size(246.dp, 336.dp)
            .padding(0.02f.dp)
        .clickable { onChooseProfile(profile) }
    ) {

        // 프로필 전체 배경
        Image(
            painter = painterResource(id = R.drawable.profile_container),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.04f.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 프로필 이미지
//            Surface(
//                modifier = Modifier.fillMaxWidth(0.6f).aspectRatio(1f),  // 프로필 이미지 크기를 비율로 설정
//                shape = RoundedCornerShape(50.dp),
//                color = Color.Transparent // Surface 투명하게 설정
//            ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f) // 1:1 비율 유지
                    .clip(CircleShape) // 원형으로 자르기
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profile.img)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop, // 짧은 부분 기준으로 중앙에 맞춰 자르기
                    modifier = Modifier.fillMaxWidth()
                )
            }
//                Image(
//                    painter = painterResource(id = R.drawable.id),
//                    contentDescription = null, // 필요에 따라 이미지 설명 추가
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
//            }

            Spacer(modifier = Modifier.height(16.dp)) // 이미지와 이름 사이 간격
            Text(
                text = profile.name,
                style = MyTypography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = PastelNavy
            )
        }
    }
}
