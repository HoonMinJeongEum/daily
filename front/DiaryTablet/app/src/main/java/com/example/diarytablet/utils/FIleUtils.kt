package com.example.diarytablet.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

fun getFilePart(fileUri: Uri, context: Context, partName: String): MultipartBody.Part {
    val file = File(fileUri.path ?: "")
    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
    return MultipartBody.Part.createFormData(partName, file.name, requestFile)

}
