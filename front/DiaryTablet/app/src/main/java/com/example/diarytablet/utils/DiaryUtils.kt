package com.example.diarytablet.utils

import StickerItem
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arthenica.ffmpegkit.FFmpegKit
import com.example.diarytablet.R
import com.example.diarytablet.model.ToolType
import createPaintForTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import android.graphics.Canvas as AndroidCanvas


suspend fun loadBitmapFromUrl(url: String): Bitmap? = withContext(Dispatchers.IO) {
    try {
        val originalBitmap = BitmapFactory.decodeStream(URL(url).openStream())
        originalBitmap?.let {
            Bitmap.createScaledBitmap(it, 150, 150, true) // 고정된 크기로 리사이즈
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// DiaryScreen.kt의 일부
@Composable
fun DrawingPlaybackView(
    drawingSteps: List<DrawingStep>,
    firstPageStickers: List<StickerItem>,
    context: Context,
    templateWidth: Int,
    templateHeight: Int,
    onVideoReady: () -> Unit
    ) {
    val overlayBitmap = remember {
        Bitmap.createBitmap(templateWidth, templateHeight, Bitmap.Config.ARGB_8888)
    }
    val overlayCanvas = remember { AndroidCanvas(overlayBitmap) }
    val currentPath = remember { Path() }
    val outputDir = File(context.filesDir, "frames").apply {
        // 폴더 내 모든 파일 삭제
        if (exists()) {
            deleteRecursively()
        }
        mkdirs()
    }
    val videoFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "drawing_playback.mp4")

    LaunchedEffect(drawingSteps) {
        overlayCanvas.drawColor(android.graphics.Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        // 한 번에 그릴 path의 개수
        val batchStepCount = 15
        var currentStepIndex = 0
        var frameCounter = 0

        while (currentStepIndex < drawingSteps.size) {
            for (i in 0 until batchStepCount) {
                if (currentStepIndex >= drawingSteps.size) break
                val step = drawingSteps[currentStepIndex]
                val paint = createPaintForTool(step.toolType, step.color, step.thickness)

                if (step.toolType == ToolType.ERASER) {
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                }

                // 각 step의 path를 overlayCanvas에 추가하여 그리기
                currentPath.addPath(step.path)
                overlayCanvas.drawPath(step.path.asAndroidPath(), paint)

                currentStepIndex++
            }

            // batchStepCount 만큼 그린 후, 프레임 비트맵을 저장
            val frameBitmap = Bitmap.createBitmap(templateWidth, templateHeight, Bitmap.Config.ARGB_8888)
            val frameCanvas = AndroidCanvas(frameBitmap)
            drawToBitmap(frameCanvas, overlayBitmap, templateWidth, templateHeight, context)

            val frameFile = File(outputDir, "frame_$frameCounter.png")
            saveBitmapToFile(frameBitmap, frameFile)
            frameCounter++

            // 지연 시간을 두고 다시 그리기
            delay(50) // 지연 시간 조정하여 속도 제어
        }

        // 모든 프레임을 비디오로 결합
        createVideoFromFrames(context, outputDir, videoFile)
        scanFile(context, videoFile)
        onVideoReady()
    }

    // Box로 캔버스 및 템플릿과 그려진 경로를 포함한 UI 구성
    Box(
        modifier = Modifier
            .width(templateWidth.dp)
            .height(templateHeight.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.TopStart
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // 템플릿 비트맵을 배경에 그림
            drawIntoCanvas { canvas ->
                val templateBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.draw_template)
                val resizedTemplateBitmap = Bitmap.createScaledBitmap(templateBitmap, templateWidth, templateHeight, true)
                canvas.nativeCanvas.drawBitmap(resizedTemplateBitmap, 0f, 0f, null)
            }

            // 현재 overlayBitmap (경로 및 지우개 효과가 포함된 비트맵) 그리기
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawBitmap(overlayBitmap, 0f, 0f, null)
            }
        }
    }
}



// drawToBitmap 함수: overlayBitmap을 포함하여 캔버스에 최종 이미지를 그리기
fun drawToBitmap(
    canvas: Canvas,
    overlayBitmap: Bitmap,
    width: Int,
    height: Int,
    context: Context
) {
    // 먼저 흰색 배경을 그리기
    canvas.drawColor(android.graphics.Color.WHITE)

    // 템플릿을 배경에 그리기
    val templateBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.draw_template)
    val resizedTemplateBitmap = Bitmap.createScaledBitmap(templateBitmap, width, height, true)
    canvas.drawBitmap(resizedTemplateBitmap, 0f, 0f, null)

    // overlayBitmap을 그리기 (경로와 지우개 효과 포함)
    canvas.drawBitmap(overlayBitmap, 0f, 0f, null)
}

// Bitmap을 파일로 저장하는 함수에 로그 추가
fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean {
    return try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        if (file.exists()) {
            Log.d("DiaryScreen", "File created successfully: ${file.absolutePath}")
            true
        } else {
            Log.e("DiaryScreen", "File creation failed: ${file.absolutePath}")
            false
        }
    } catch (e: Exception) {
        Log.e("DiaryScreen", "Error saving bitmap to file: ${e.message}")
        false
    }
}


// 동영상 생성 함수
fun createVideoFromFrames(context: Context, framesDir: File, outputFile: File) {
    val command = "-y -framerate 30 -i ${framesDir.absolutePath}/frame_%d.png -c:v mpeg4 -qscale:v 2 -pix_fmt yuv420p ${outputFile.absolutePath}"

    FFmpegKit.executeAsync(command) { session ->
        if (session.returnCode.isValueSuccess) {
            Log.d("DrawingPlaybackView", "Video created successfully at: ${outputFile.absolutePath}")
        } else {
            Log.e("DrawingPlaybackView", "Failed to create video: ${session.output}")
        }
    }
}


// 미디어 스캔을 수행하여 갤러리에 파일 추가
fun scanFile(context: Context, videoFile: File) {
    MediaScannerConnection.scanFile(context, arrayOf(videoFile.absolutePath), null) { path, uri ->
        Log.d("DrawingPlaybackView", "Scanned $path:")
        Log.d("DrawingPlaybackView", "-> uri=$uri")
    }
}


data class DrawingStep(
    val path: Path,
    val color: Color,
    val thickness: Float,
    val toolType: ToolType // toolType 추가
)

suspend fun savePageImagesWithTemplate(
    bitmapsList: List<Bitmap>,
    context: Context,
    leftBoxWidth: Dp,
    boxHeight: Dp,
    firstPageStickers: List<StickerItem>,
    padding: Int = 16 // 바깥 박스를 위한 추가 패딩
): List<File> {
    // Dp를 픽셀로 변환
    val density = context.resources.displayMetrics.density
    val outerBoxWidthPx = (leftBoxWidth.toPx(density) + padding * 2).toInt()
    val outerBoxHeightPx = (boxHeight.toPx(density) + padding * 2).toInt()

    return withContext(Dispatchers.IO) {
        bitmapsList.mapIndexed { index, drawingBitmap ->
            val targetWidth = outerBoxWidthPx - padding * 2
            val targetHeight = outerBoxHeightPx - padding * 2

            // 바깥 박스 배경용 흰색 Bitma
            val outerBoxBackground = Bitmap.createBitmap(outerBoxWidthPx, outerBoxHeightPx, Bitmap.Config.ARGB_8888)
            val outerCanvas = Canvas(outerBoxBackground)
            val paint = android.graphics.Paint().apply { color = android.graphics.Color.WHITE }
            outerCanvas.drawRect(0f, 0f, outerBoxWidthPx.toFloat(), outerBoxHeightPx.toFloat(), paint) // 흰색으로 칠하기

            // 템플릿 이미지 불러와서 크기 조정
            val templateBitmap = if (index == 0) {
                BitmapFactory.decodeResource(context.resources, R.drawable.draw_template)
            } else {
                BitmapFactory.decodeResource(context.resources, R.drawable.write_template)
            }
            val resizedTemplateBitmap = Bitmap.createScaledBitmap(templateBitmap, targetWidth - padding * 4, targetHeight - padding * 4, true)
            val resizedDrawingBitmap = Bitmap.createScaledBitmap(drawingBitmap, targetWidth, targetHeight, true)
            // 중앙 위치 계산
            val centerX = (outerBoxWidthPx - targetWidth + padding*4) / 2f
            val centerY = (outerBoxHeightPx - targetHeight + padding*4) / 2f
            // 바깥 박스의 중앙에 템플릿 및 비트맵을 그리기
            outerCanvas.drawBitmap(resizedTemplateBitmap, centerX, centerY, null)
            outerCanvas.drawBitmap(resizedDrawingBitmap, centerX, centerY, null)
            // 스티커 추가 (첫 번째 페이지에만 스티커 표시)
            if (index == 0) {
                firstPageStickers.forEach { sticker ->
                    val stickerX = centerX + sticker.position.value.x
                    val stickerY = centerY + sticker.position.value.y
                    outerCanvas.drawBitmap(sticker.bitmap, stickerX, stickerY, null)
                }
            }
            // 이미지 크기 줄이기
            val finalBitmap = resizeBitmap(outerBoxBackground, 1450, 1000) // 저장 크기 설정 (필요시 조정 가능)
            // 압축하여 파일로 저장
            val file = File(context.filesDir, "drawing_combined_$index.jpg")
            compressBitmap(finalBitmap, file, quality = 50)

            if (file.exists()) {
                Log.d("DiaryScreen", "File created successfully: ${file.absolutePath}")
            } else {
                Log.e("DiaryScreen", "File creation failed: ${file.absolutePath}")
            }

            file // 파일 반환
        }
    }
}

// Dp를 픽셀로 변환하는 확장 함수
fun Dp.toPx(density: Float): Float = this.value * density

// 해상도를 조절하는 함수
fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(bitmap, width, height, true)
}

// 품질을 낮춰서 압축하는 함수
fun compressBitmap(bitmap: Bitmap, outputFile: File, quality: Int = 30) {
    FileOutputStream(outputFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
    }
}