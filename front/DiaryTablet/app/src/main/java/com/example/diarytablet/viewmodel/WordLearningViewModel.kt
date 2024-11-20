package com.example.diarytablet.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.R
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import com.example.diarytablet.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import android.graphics.Color as AndroidColor
import kotlinx.coroutines.withTimeout

@HiltViewModel
class WordLearningViewModel @Inject constructor(
    private val userStore: UserStore,
    private val wordRepository: WordRepository
) : ViewModel() {

    val username: Flow<String> = userStore.getValue(UserStore.KEY_PROFILE_NAME)

    private val _wordList = mutableStateOf<List<WordResponseDto>>(emptyList())
    val wordList: State<List<WordResponseDto>> get() = _wordList

    private val _learnedWordList = mutableStateOf<List<WordRequestDto>>(emptyList())
    val learnedWordList: State<List<WordRequestDto>> get() = _learnedWordList

    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    init {
        getWordList() // 초기 화면 로딩 시 프로필 리스트 가져오기
    }

    // 단어 목록을 가져오는 함수
    fun getWordList() {
        isLoading.value = true
        viewModelScope.launch {
            try {


                val words = wordRepository.getWordList()
                _wordList.value = words
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }



    // 단어 검증 함수
    suspend fun checkWordValidate(context: Context, word: WordResponseDto, writtenBitmap: Bitmap): Int {
        isLoading.value = true
        return withContext(Dispatchers.IO) {
            try {
                withTimeout(8000) { // 5초 타임아웃 설정
                    val mergedBitmap = mergeBitmapWithTemplate(context, writtenBitmap)
                    val fileName = "word_image_${word.id}.jpg"

                    val writeFilePart = bitmapToMultipart(context, mergedBitmap, fileName)
                    val wordPart = word.word.toRequestBody("text/plain".toMediaTypeOrNull())

                    val validationResponse = wordRepository.checkWordValidate(writeFilePart, wordPart)

                    val statusCode = validationResponse.code()
                    Log.d("function", "success $statusCode")

                    when (statusCode) {
                        200 -> {
                            _learnedWordList.value += WordRequestDto(id = word.id, image = writeFilePart)
                            Log.d("gon", "Validation successful")
                        }
                        400 -> Log.d("gon", "Bad Request - Unrecognized text")
                        422 -> Log.d("gon", "Validation error - Incorrect format")
                        else -> Log.d("gon", "Unexpected error: $statusCode")
                    }
                    statusCode
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.TimeoutCancellationException) {
                    Log.d("gon", "Timeout occurred during word validation")
                } else {
                    Log.d("gon", "Error: ${e.message}")
                }
                errorMessage.value = e.message
                -1
            } finally {
                isLoading.value = false
            }
        }
    }



    private suspend fun mergeBitmapWithTemplate(
        context: Context,
        drawnBitmap: Bitmap,
    ): Bitmap = withContext(Dispatchers.IO) {
        // 템플릿 생성
        val width = 1500 // 드로잉 비트맵의 1.5배 크기
        val height = 800

        val templateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            eraseColor(AndroidColor.WHITE) // 템플릿 배경 흰색
        }

        val combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)

        // 템플릿 배경 그리기
        canvas.drawBitmap(templateBitmap, 0f, 0f, null)

        // 중앙 좌표 계산
        val left = (width - drawnBitmap.width) / 2f
        val top = (height - drawnBitmap.height) / 2f

        // 디버그 로그로 좌표 확인
        Log.d("BitmapMerge", "Width: $width, Height: $height")
        Log.d("BitmapMerge", "Bitmap Width: ${drawnBitmap.width}, Bitmap Height: ${drawnBitmap.height}")
        Log.d("BitmapMerge", "Calculated Left: $left, Top: $top")

        // 드로잉 비트맵 중앙에 배치
        canvas.drawBitmap(drawnBitmap, left, top, null)

        combinedBitmap
    }


    // 비트맵을 파일로 저장 후 MultipartBody.Part로 변환
    private suspend fun bitmapToMultipart(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ): MultipartBody.Part = withContext(Dispatchers.IO) {
        val file = File(context.cacheDir, fileName)
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
        }
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        MultipartBody.Part.createFormData("writeFile", file.name, requestFile)
    }

suspend fun finishWordLearning() {
    isLoading.value = true
    withContext(Dispatchers.IO) {
        try {
            // ids와 images 리스트 생성
            val idsJson = _learnedWordList.value.joinToString(",", prefix = "[", postfix = "]") { it.id.toString() }
            val idsRequestBody = idsJson.toRequestBody("application/json".toMediaTypeOrNull())
            val imageFiles = _learnedWordList.value.map { it.image }

            // 요청 전송
            val response = wordRepository.finishWordLearning(idsRequestBody, imageFiles)
            if (response.isSuccessful) {
                Log.d("gon", "Word learning session completed successfully.")
            } else {
                Log.e("gon", "Error in finishing word learning: ${response}")
            }
        } catch (e: Exception) {
            errorMessage.value = e.message
            Log.e("gon", "Exception in finishWordLearning: ${e.message}")
        } finally {
            isLoading.value = false
        }
    }
}
//
//
}
