package com.example.diarytablet.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.domain.repository.DiaryRepository
import com.example.diarytablet.model.StickerStock
import com.google.gson.Gson
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

    private val _userStickers = MutableLiveData<List<StickerStock>>()
    val userStickers: LiveData<List<StickerStock>> get() = _userStickers
    // 에러 상태를 관리할 LiveData

    private val _responseMessage = MutableLiveData<String?>()
    val responseMessage: LiveData<String?> get() = _responseMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> get() = _isError

    fun fetchUserStickers() {
        viewModelScope.launch {
            try {
                val response = diaryRepository.getUserStickers()
                if (response.isSuccessful) {
                    _userStickers.postValue(response.body())
                } else {
                    Log.e("DiaryViewModel", "스티커 불러오기 실패: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e("DiaryViewModel", "스티커 불러오는 중 오류 발생", e)
            }
        }
    }

    fun uploadDiary(context: Context, drawUri: Uri, writeUri: Uri, videoUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true // 로딩 시작
            _responseMessage.postValue(null) // 이전 메시지 초기화

            val drawFilePart = getFilePart(context, drawUri, "drawFile")
            val writeFilePart = getFilePart(context, writeUri, "writeFile")
            val videoFilePart = getFilePart(context, videoUri, "videoFile", "video/mp4")

            if (drawFilePart == null || writeFilePart == null || videoFilePart == null) {
                Log.e("DiaryViewModel", "One or more file parts are null. Upload skipped.")
                _isLoading.value = false
                _responseMessage.postValue("파일 생성 실패!")
                return@launch
            }

            try {
                val response = diaryRepository.uploadDiary(drawFilePart, writeFilePart, videoFilePart)

                _isLoading.value = false // 응답 도착 -> 로딩 종료
                if (response.isSuccessful && response.code() == 200) {
                    _responseMessage.postValue("그림 일기 작성 완료!")
                } else if (response.code() == 409) {
                    _responseMessage.postValue("일기는 하루에\n한 번만 쓸 수 있어요!")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = errorBody?.let { parseErrorResponse(it) }
                    _responseMessage.postValue(errorResponse?.msg ?: "그림일기를 다시 그려주세요!")
                }
            } catch (e: Exception) {
                Log.e("DiaryViewModel", "Exception occurred during upload", e)
                _isLoading.value = false
                _responseMessage.postValue("그림일기를 다시 그려주세요!")
            }
        }
    }


    // 에러 응답을 위한 데이터 클래스
    data class ErrorResponse(val status: Int, val msg: String)

    // JSON 에러 응답 파싱 함수
    fun parseErrorResponse(errorBody: String): ErrorResponse? {
        return try {
            // JSON 파싱 (예: Gson 또는 kotlinx.serialization 사용 가능)
            Gson().fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            Log.e("DiaryViewModel", "Error parsing error response: ${e.message}")
            null
        }
    }
    // 에러 상태 초기화 함수
    fun clearResponseMessage() {
        _responseMessage.postValue(null)
    }

    private fun getFilePart(context: Context, fileUri: Uri, partName: String, mimeType: String = "image/jpeg"): MultipartBody.Part? {
        val inputStream = context.contentResolver.openInputStream(fileUri) ?: return null
        val file = File.createTempFile("temp_", ".${mimeType.split("/")[1]}", context.cacheDir)
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
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
    private fun getFilePart(context: Context, fileUri: Uri, partName: String, mimeType: String = "image/jpeg"): MultipartBody.Part? {
        val inputStream = context.contentResolver.openInputStream(fileUri) ?: return null
        val file = File.createTempFile("temp_", ".${mimeType.split("/")[1]}", context.cacheDir)
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
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

