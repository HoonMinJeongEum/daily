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
    suspend fun checkWordValidate(context: Context, word: WordResponseDto, writtenBitmap: Bitmap):Int {
        isLoading.value = true
        return withContext(Dispatchers.IO) {
            try {

                val mergedBitmap = mergeBitmapWithTemplate(context, writtenBitmap)
                val fileName = "word_image_${word.id}.jpg"

                val writeFilePart  = bitmapToMultipart(context, mergedBitmap,fileName)

                val wordPart = word.word.toRequestBody("text/plain".toMediaTypeOrNull())

                val validationResponse = wordRepository.checkWordValidate(writeFilePart,wordPart)

                val statusCode = validationResponse.code()
                Log.d("function","success ${statusCode}")

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
            } catch (e: Exception) {
                Log.d("gon", "${e.message}")

                errorMessage.value = e.message
                -1

            } finally {
                isLoading.value = false

            }
        }
    }



    // 템플릿 이미지와 그려진 비트맵 합성 함수
    private suspend fun mergeBitmapWithTemplate(
        context: Context,
        drawnBitmap: Bitmap,
        width: Int = 1500,
        height: Int = 800
    ): Bitmap = withContext(Dispatchers.IO) {
        // 템플릿 생성
        val templateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            eraseColor(AndroidColor.WHITE) // 템플릿 배경을 흰색으로 설정
        }

        val combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)

        // 템플릿과 드로잉 비트맵을 순서대로 합성
        canvas.drawBitmap(templateBitmap, 0f, 0f, null)

        // 글자 비트맵을 배경의 가운데에 그리기
        canvas.drawBitmap(
            drawnBitmap,
            ((width - drawnBitmap.width) / 2).toFloat(),
            ((height - drawnBitmap.height) / 2).toFloat(),
            null
        )
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

//
//    private suspend fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
//        return withContext(Dispatchers.IO) {
//            val file = File(context.cacheDir, fileName)
//            file.outputStream().use { out ->
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
//                out.flush()
//            }
//            file
//        }
//    }
//
//    private fun createMultipartBodyPart(file: File, paramName: String): MultipartBody.Part {
//        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//        return MultipartBody.Part.createFormData(paramName, file.name, requestFile)
//    }
//
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
//    suspend fun savePageImagesWithTemplate(bitmapsList: List<Bitmap>, context: Context): List<File> {
//        return withContext(Dispatchers.IO) {
//            bitmapsList.mapIndexed { index, drawingBitmap ->
//                // 박스 배경, 템플릿, 그림판의 크기를 동일하게 설정
//                val targetWidth = 2000
//                val targetHeight = 1500
//
//                // 박스 배경 이미지 불러와서 크기 조정
//                val boxBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.diary_box)
//                val resizedBoxBitmap = Bitmap.createScaledBitmap(boxBitmap, targetWidth, targetHeight, true)
//
//                // 템플릿 이미지 불러와서 크기 조정
//                val templateBitmap = if (index == 0) {
//                    BitmapFactory.decodeResource(context.resources, R.drawable.draw_template)
//                } else {
//                    BitmapFactory.decodeResource(context.resources, R.drawable.write_template)
//                }
//                val resizedTemplateBitmap = Bitmap.createScaledBitmap(templateBitmap, targetWidth, targetHeight, true)
//
//                // 그림판 이미지 크기 조정
//                val resizedDrawingBitmap = Bitmap.createScaledBitmap(drawingBitmap, targetWidth, targetHeight, true)
//
//                // 같은 크기의 새로운 비트맵 생성
//                val combinedBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
//                val canvas = Canvas(combinedBitmap)
//
//                // 박스 이미지 그리기
//                canvas.drawBitmap(resizedBoxBitmap, 0f, 0f, null)
//
//                // 그림판 그리기
//                canvas.drawBitmap(resizedDrawingBitmap, 0f, 0f, null)
//
//                // 템플릿 그리기
//                canvas.drawBitmap(resizedTemplateBitmap, 0f, 0f, null)
//
//                // 이미지 크기 줄이기
//                val finalBitmap = resizeBitmap(combinedBitmap, 1000, 750)
//
//                // 압축하여 파일로 저장
//                val file = File(context.filesDir, "drawing_combined_$index.jpg")
//                compressBitmap(finalBitmap, file, quality = 50)
//
//                if (file.exists()) {
//                    Log.d("DiaryScreen", "File created successfully: ${file.absolutePath}")
//                } else {
//                    Log.e("DiaryScreen", "File creation failed: ${file.absolutePath}")
//                }
//
//                file // 파일 반환
//            }
//        }
//    }
//
//    // 해상도를 조절하는 함수
//    fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
//        return Bitmap.createScaledBitmap(bitmap, width, height, true)
//    }
//
//    // 품질을 낮춰서 압축하는 함수
//    fun compressBitmap(bitmap: Bitmap, outputFile: File, quality: Int = 30) {
//        FileOutputStream(outputFile).use { out ->
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
//        }
//    }
}
