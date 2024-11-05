package com.example.diaryApp.ui.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.Black
import com.example.diaryApp.ui.theme.PastelSkyBlue
import com.example.diaryApp.viewmodel.ProfileViewModel

@Composable
fun CreateProfile(
    profileViewModel: ProfileViewModel,
    onCancel: () -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
        selectedImageUri = uri
        profileViewModel.memberImg.value = selectedImageUri
    }

    Box(
        modifier = Modifier
            .size(394.dp, 165.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_container),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .border(2.dp, Color.LightGray, RoundedCornerShape(50.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .border(2.dp, Color.LightGray, RoundedCornerShape(50.dp))
                    ) {
                        DailyButton(
                            text = "프로필 추가",
                            fontSize = 12,
                            textColor = Black,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                            backgroundColor = Color.Transparent,
                            cornerRadius = 60,
                            width = 80,
                            height = 80,
                            onClick = { launcher.launch("image/*") },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (profileViewModel.memberName.value.isEmpty()) {
                    // placeholder 용 Text
                    Text(
                        text = "이름",
                        modifier = Modifier
                            .alpha(0.5f),
                        fontSize = 12.sp
                    )
                }
                
                BasicTextField(
                    value = profileViewModel.memberName.value,
                    onValueChange = {
                        if (it.length <= 5) {
                            profileViewModel.memberName.value = it
                        }},
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .background(Color.Transparent)
                )
            }

            DailyButton(
                text = "추가",
                fontSize = 18,
                textColor = Black,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                backgroundColor = PastelSkyBlue,
                cornerRadius = 50,
                width = 90,
                height = 35,
                onClick = {
                    Log.d("JoinScreen","click the create profile button")
                    if (profileViewModel.memberName.value.isNotBlank()) {
                        profileViewModel.addProfile(
                            onSuccess = {
                                Log.d("JoinScreen", "JoinSuccess called")
                                onCancel()
                            },
                            onError = {
                                Log.d("JoinScreen", "JoinSuccess Fail")
                                onCancel()
                            }
                        )
                    }
                }
            )
            Image(
                painter = painterResource(id = R.drawable.profile_cancel),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(20.dp)
                    .offset(x=20.dp, y=(-40).dp)
                    .clickable(onClick = { onCancel() })
            )
        }
    }
}