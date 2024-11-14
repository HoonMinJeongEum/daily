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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.theme.DeepPastelNavy
import java.io.File
import java.io.FileOutputStream
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged

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

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            val filePath = getFilePathFromUri(context, it)
            filePath?.let { path -> onEditProfileClick(path) }
        }
    }

    if (isModalVisible) {
        Dialog(
            onDismissRequest = {
                onDismiss()
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        ) {
            Box(
                modifier = Modifier
                    .size(screenHeight * 0.6f)
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

                    Box(
                        modifier = Modifier
                            .size(screenHeight * 0.25f)
                            .clip(CircleShape)
                            .clickable {
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

                    Spacer(modifier = Modifier.height(screenHeight * 0.03f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isEditing) {
                            TextField(
                                value = editedName,
                                onValueChange = { editedName = it },
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
                                        Text(text = userName)
                                    }
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color(0xFFF0F0F0),
                                    focusedIndicatorColor = DeepPastelNavy,
                                    unfocusedIndicatorColor = Color.Gray
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 38.sp
                                )
                            )

                            Spacer(modifier = Modifier.width(screenHeight * 0.02f))
                            BasicButton(
                                text = "완료",
                                imageResId = 11,
                                onClick = {
                                    onEditNameClick(editedName)
                                    isEditing = false
                                }
                            )
                        } else {
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
                                    .size(screenHeight * 0.05f)
                                    .clickable { isEditing = true }
                            )
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