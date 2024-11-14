package com.example.diarytablet.ui.components.modal

import CropActivity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.theme.DarkRed
import com.example.diarytablet.ui.theme.DeepPastelNavy
import com.example.diarytablet.viewmodel.NavBarViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

import androidx.compose.material3.TextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileModal(
    isModalVisible: Boolean,
    onDismiss: () -> Unit,
    profileImageUrl: String,
    userName: String,
    onEditProfileClick: (String) -> Unit,
    onEditNameClick: (String) -> Unit,
    screenWidth: Dp,
    screenHeight: Dp
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userName) }
    var croppedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showWarning by remember { mutableStateOf(false) } // 경고 문구 표시 여부
    val context = LocalContext.current

    // 모달이 열릴 때 초기화
    if (isModalVisible) {
        isEditing = false
        editedName = userName
        showWarning = false
    }

    // 이미지 선택 및 크롭 작업을 수행하는 런처 설정
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            croppedImageUri = it
            val filePath = getFilePathFromUri(context, it)
            filePath?.let { path -> onEditProfileClick(path) }
        }
    }

    if (isModalVisible) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(dismissOnClickOutside = false)
        ) {
            Box(
                modifier = Modifier
                    .size(screenHeight * 0.6f, screenHeight * 0.65f)
                    .background(Color.White, shape = RoundedCornerShape(screenWidth * 0.02f))
                    .padding(screenHeight * 0.04f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.02f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onDismiss() }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mission_close),
                            contentDescription = "Close",
                            modifier = Modifier.size(screenHeight * 0.05f)
                        )
                    }

                    AsyncImage(
                        model = croppedImageUri ?: profileImageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(screenHeight * 0.25f)
                            .clip(CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.03f))


                        if (isEditing) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextField(
                                    value = editedName,
                                    onValueChange = {
                                        editedName = it
                                        showWarning = it.length > 5
                                    },
                                    modifier = Modifier.width(screenWidth * 0.12f),

                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Spacer(modifier = Modifier.width(screenHeight * 0.02f))

                                BasicButton(
                                    text = "완료",
                                    imageResId = 11,
                                    onClick = {
                                        onEditNameClick(editedName)
                                        isEditing = false
                                    },
                                    enabled = !showWarning // 5글자 초과 시 비활성화
                                )
                            }

                                if (showWarning) {
                                    Text(
                                        text = "닉네임은 5글자 이하로 입력해주세요.",
                                        color = DarkRed,
                                        fontSize = (screenHeight.value * 0.025f).sp
                                    )
                                }





                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                            Text(
                                text = userName,
                                fontSize = (screenHeight.value * 0.07f).sp,
                                color = DeepPastelNavy
                            )
                            Spacer(modifier = Modifier.width(screenHeight * 0.025f))
                            Image(
                                painter = painterResource(id = R.drawable.pencil),
                                contentDescription = "Edit Name",
                                modifier = Modifier
                                    .size(screenHeight * 0.07f)
                                    .clickable { isEditing = true }
                            )
                        }
                    }
                }
            }
        }
    }
}


// 여기에 있는 getFilePathFromUri와 getFileName 함수는 그대로 사용


private fun getFilePathFromUri(context: Context, uri: Uri): String? {
    val file = File(context.cacheDir, getFileName(context, uri))
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return file.absolutePath
}

private fun getFileName(context: Context, uri: Uri): String {
    var name = "temp_image"
    val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst()) {
            name = it.getString(nameIndex)
        }
    }
    return name
}
