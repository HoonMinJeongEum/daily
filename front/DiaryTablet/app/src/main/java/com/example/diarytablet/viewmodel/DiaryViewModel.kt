//package com.example.diarytablet.viewmodel
//
//import android.content.Context
//import android.net.Uri
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.diarytablet.domain.repository.DiaryRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import java.io.File
//import javax.inject.Inject
//
//@HiltViewModel
//class DiaryViewModel @Inject constructor(
//    private val diaryRepository: DiaryRepository,
//) : ViewModel() {
//
//    fun uploadDiary(context: Context, drawUri: Uri, writeUri: Uri) {
//        viewModelScope.launch {
//            val drawFilePart = getFilePart(context, drawUri, "drawFile")
//            val writeFilePart = getFilePart(context, writeUri, "writeFile")
//
//            if (drawFilePart == null || writeFilePart == null) {
//                Log.e("DiaryViewModel", "File parts are null. Upload skipped.")
//                return@launch
//            }
//
//            try {
//                val response = diaryRepository.uploadDiary(drawFilePart, writeFilePart)
//                if (response.isSuccessful) {
//                    Log.d("DiaryViewModel", "Image upload successful")
//                } else {
//                    Log.e("DiaryViewModel", "Image upload failed: ${response.code()?.toString()} - ${response.errorBody()?.toString()}")
//                }
//            } catch (e: Exception) {
//                Log.e("DiaryViewModel", "Exception occurred during upload", e)
//            }
//        }
//    }
//
//    private fun getFilePart(context: Context, fileUri: Uri, partName: String): MultipartBody.Part? {
//        val filePath = fileUri.path ?: return null
//        val file = File(filePath)
//
//        // 서버가 올바르게 파일 형식을 처리할 수 있도록 Content-Type을 설정합니다.
//        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
//        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
//    }
//
//
//}

package com.example.diarytablet.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.domain.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
) : ViewModel() {

    fun uploadDiary(context: Context, drawUri: Uri, writeUri: Uri) {
        viewModelScope.launch {
            val drawFilePart = getFilePart(context, drawUri, "drawFile")
            val writeFilePart = getFilePart(context, writeUri, "writeFile")

            if (drawFilePart == null || writeFilePart == null) {
                Log.e("DiaryViewModel", "File parts are null. Upload skipped.")
                return@launch
            }

            try {
                val response = diaryRepository.uploadDiary(drawFilePart, writeFilePart)
                if (response.isSuccessful) {
                    Log.d("DiaryViewModel", "Image upload successful")
                } else {
                    Log.e("DiaryViewModel", "Image upload failed: ${response.code()?.toString()} - ${response.errorBody()?.toString()}")
                }
            } catch (e: Exception) {
                Log.e("DiaryViewModel", "Exception occurred during upload", e)
            }
        }
    }

    private fun getFilePart(context: Context, fileUri: Uri, partName: String): MultipartBody.Part? {
        val inputStream = context.contentResolver.openInputStream(fileUri) ?: return null
        val file = File.createTempFile("temp_", ".jpg", context.cacheDir)
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }


    private fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var filePath: String? = null
        val cursor = context.contentResolver.query(contentUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow("_data")
                filePath = it.getString(columnIndex)
            }
        }
        return filePath
    }
}
