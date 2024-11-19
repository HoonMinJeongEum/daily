package com.example.diarytablet.ui.components.modal

import android.content.Context
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import com.example.diarytablet.utils.clearFocusOnClick
import com.example.diarytablet.utils.playButtonSound


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
    var isEditing by remember(key1 = isModalVisible) { mutableStateOf(false) }
    var editedName by remember(key1 = isModalVisible) { mutableStateOf(userName) }
    var imageUri by remember(key1 = isModalVisible) { mutableStateOf<Uri?>(null) }
    val focusRequester = remember { FocusRequester() }
    var isTextFieldFocused by remember(key1 = isModalVisible) { mutableStateOf(false) }
    var isProfileUpdating by remember { mutableStateOf(false) }
    var showWarning by remember { mutableStateOf(false) } // 경고 문구 표시 여부
    val context = LocalContext.current

    // 모달이 열릴 때 초기화
    if (isModalVisible) {
        isEditing = false
        editedName = ""
        showWarning = false
    }

    // 이미지 선택 및 크롭 작업을 수행하는 런처 설정
    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                isProfileUpdating = true // 프로필 업데이트 중
                val filePath = getFilePathFromUri(context, it)
                filePath?.let { path ->
                    onEditProfileClick(path)
                    isProfileUpdating = false // 업데이트 완료
                }
            }
        }

    if (isModalVisible) {
        Dialog(
            onDismissRequest = {
                if (!isProfileUpdating) onDismiss()
                               },
            properties = DialogProperties(dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clearFocusOnClick()

            )
            {
                Box(
                    modifier = Modifier
                        .size(screenHeight * 0.6f, screenHeight * 0.65f)
                        .background(Color.White, shape = RoundedCornerShape(screenWidth * 0.02f))
                        .padding(screenHeight * 0.04f)
                        .clearFocusOnClick()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(screenHeight * 0.02f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable(
                                    enabled = !isProfileUpdating
                                ) {
                                    onDismiss()
                                    playButtonSound(context, R.raw.all_button)
                                }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.mission_close),
                                contentDescription = "Close",
                                modifier = Modifier.size(screenHeight * 0.05f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(screenHeight * 0.25f)
                                .clip(CircleShape)
                                .clickable(
                                    enabled = !isProfileUpdating // 프로필 업데이트 중 비활성화
                                ) {
                                    isProfileUpdating = true
                                    playButtonSound(context, R.raw.all_button)
                                    imagePickerLauncher.launch("image/*")
                                }
                        ) {
                            AsyncImage(
                                model = imageUri ?: profileImageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f))
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.pencil),
                                contentDescription = "Edit Profile",
                                modifier = Modifier
                                    .size(screenHeight * 0.05f)
                                    .align(Alignment.Center),
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(screenHeight * 0.005f))
                        if (showWarning) {
                            Text(
                                text = "닉네임은 5글자 이하로 입력해주세요.",
                                color = DarkRed,
                                fontSize = (screenHeight.value * 0.025f).sp
                            )
                        } else {
                            // 경고 메시지가 없을 때도 동일한 높이의 Spacer를 추가하여 레이아웃 차이를 없앰
                            Spacer(modifier = Modifier.height(screenHeight * 0.025f))
                        }
                        Spacer(modifier = Modifier.height(screenHeight * 0.005f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isEditing) {
                                TextField(
                                    value = editedName,
                                    onValueChange = {
                                        if (it.length <= 5) { // 5글자 이하로 제한
                                            editedName = it
                                        } else {
                                            showWarning = true
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .width(screenHeight * 0.3f)
                                        .height(screenHeight * 0.1f)
                                        .focusRequester(focusRequester)
                                        .onFocusChanged { focusState ->
                                            isTextFieldFocused = focusState.isFocused
                                            if (focusState.isFocused && editedName == userName) {
                                                editedName = ""
                                            }
                                        },
                                    placeholder = {
                                        if (!isTextFieldFocused) {
                                            Text(
                                                text = userName,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = (screenHeight.value * 0.05f).sp
                                                )                                                )
                                        }
                                    },
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = Color(0xFFF0F0F0),
                                        focusedIndicatorColor = DeepPastelNavy,
                                        unfocusedIndicatorColor = Color.Gray
                                    ),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = (screenHeight.value * 0.05f).sp
                                    ),
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                                            ),
                                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                                        onDone = {
                                            if (editedName.isNotBlank() && editedName != userName) {
                                                onEditNameClick(editedName)
                                                isEditing = false
                                            }
                                        }
                                    )
                                )

                                Spacer(modifier = Modifier.width(screenHeight * 0.02f))
                                BasicButton(
                                    text = "완료",
                                    imageResId = 11,
                                    onClick = {
                                        onEditNameClick(editedName)
                                        isEditing = false
                                    },
                                    enabled = !editedName.isEmpty()
                                )

                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = userName,
                                        fontSize = (screenHeight.value * 0.05f).sp,
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
    }
}

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