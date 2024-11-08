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
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    /**
     * 두 개의 이미지 파일을 서버에 업로드하는 함수.
     *
     * @param context Context - 파일 경로에 접근하기 위해 필요.
     * @param drawUri Uri - 그림 파일의 URI.
     * @param writeUri Uri - 작성 파일의 URI.
     */
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
                    Log.e("DiaryViewModel", "Image upload failed: ${response.code()} - ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e("DiaryViewModel", "Exception occurred during upload", e)
            }
        }
    }

    /**
     * Uri를 사용하여 MultipartBody.Part 객체를 생성하는 함수.
     *
     * @param context Context - 파일 경로에 접근하기 위해 필요.
     * @param fileUri Uri - 파일의 URI.
     * @param partName String - Multipart 파트의 이름.
     * @return MultipartBody.Part? - 파일이 존재하지 않으면 null을 반환.
     */
    private fun getFilePart(context: Context, fileUri: Uri, partName: String): MultipartBody.Part? {
        // Uri를 통해 InputStream을 열고, 이를 임시 파일로 저장
        val inputStream = context.contentResolver.openInputStream(fileUri) ?: return null
        val file = File.createTempFile("temp_", ".jpg", context.cacheDir)
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        // 파일을 RequestBody로 변환하여 MultipartBody.Part로 생성
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    /**
     * URI에서 실제 파일 경로를 추출하는 함수.
     *
     * @param context Context - 파일 경로에 접근하기 위해 필요.
     * @param contentUri Uri - 파일의 URI.
     * @return String? - 파일의 실제 경로.
     */
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
