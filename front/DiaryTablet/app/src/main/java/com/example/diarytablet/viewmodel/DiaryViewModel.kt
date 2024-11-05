package com.example.diarytablet.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.domain.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
) : ViewModel() {

    fun uploadDiary(drawUri: Uri, writeUri: Uri) {
        viewModelScope.launch {
            val drawFilePart = getFilePart(drawUri, "drawFile")
            val writeFilePart = getFilePart(writeUri, "writeFile")
//            diaryRepository.uploadDiary(drawFilePart, writeFilePart)
            try {
                val response = diaryRepository.uploadDiary(drawFilePart, writeFilePart)
                if (response.isSuccessful) {
                    // 업로드 성공
                    Log.d("DiaryViewModel", "Image upload successful")
                } else {
                    // 업로드 실패
                    Log.e("DiaryViewModel", "Image upload failed: ${response.code()}")
                }
            } catch (e: Exception) {
                // 예외 발생 시
                Log.e("DiaryViewModel", "Exception occurred during upload", e)
            }
        }
    }

    private fun getFilePart(fileUri: Uri, partName: String): MultipartBody.Part {
        val file = File(fileUri.path ?: "")
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}