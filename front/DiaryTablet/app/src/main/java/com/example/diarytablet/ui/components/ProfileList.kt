package com.example.diarytablet.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.ui.theme.PastelNavy

@Composable
fun ProfileList(
    modifier: Modifier = Modifier,
    profileList: List<Profile>,
    onChooseProfile: (Profile) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth().wrapContentHeight()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.04f, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.2f))

            profileList.forEach { profile ->
                val profileWithDefaultImg = if (profile.img.isNullOrEmpty()) {
                    profile.copy(img = "https://example.com/default-image.jpg")
                } else {
                    profile
                }
                ProfileItem(profile = profileWithDefaultImg, onChooseProfile = onChooseProfile, screenWidth, screenHeight)
            }
        }
    }
}

@Composable
fun ProfileItem(
    profile: Profile,
    onChooseProfile: (Profile) -> Unit,
    containerWidth: Dp,
    containerHeight: Dp
) {
    Box(
        modifier = Modifier
            .width(containerWidth * 0.2f) // ProfileItem의 전체 너비를 조정
            .aspectRatio(0.75f) // 너비 대비 높이 비율 설정
            .clickable { onChooseProfile(profile) }
//            .padding(containerWidth * 0.02f)
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_container),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )


        Column(
            modifier = Modifier
                .fillMaxSize(),
//                .padding(containerWidth * 0.04f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profile.img)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(containerHeight * 0.05f))

            Text(
                text = profile.name,
                style = MyTypography.bodyLarge.copy(
                    fontSize = (containerWidth.value * 0.03f).sp
                ),
                color = PastelNavy,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = containerWidth * 0.02f)
            )
        }
    }
}
