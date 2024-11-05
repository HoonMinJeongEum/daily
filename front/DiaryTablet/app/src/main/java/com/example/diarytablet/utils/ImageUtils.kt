import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import java.io.File
import java.io.FileOutputStream

@Composable
fun CaptureAndSaveComposable(
    context: Context,
    modifier: Modifier = Modifier,
    fileName: String,
    content: @Composable () -> Unit
) {
    var size by remember { mutableStateOf(IntSize(0, 0)) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                size = coordinates.size
                // 캡처 크기 설정
                imageBitmap = ImageBitmap(size.width, size.height, ImageBitmapConfig.Argb8888)
            }
            .graphicsLayer() // 캡처할 레이어 추가
            .drawWithCache {
                onDrawWithContent {
                    imageBitmap?.let { image ->
                        drawIntoCanvas { canvas ->
                            // Offset과 Paint 객체를 명시적으로 설정합니다.
                            val paint = androidx.compose.ui.graphics.Paint()
                            canvas.drawImage(image, Offset.Zero, paint)
                        }
                    }
                    drawContent() // 원래 콘텐츠를 그립니다.
                }
            }
    ) {
        content() // 콘텐츠 렌더링
    }

    LaunchedEffect(imageBitmap) {
        imageBitmap?.let {
            saveBitmapToFile(context, it.asAndroidBitmap(), fileName)
        }
    }
}

fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
    val file = File(context.cacheDir, fileName)
    FileOutputStream(file).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }
    return file
}
