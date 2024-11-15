//import android.content.Context
//import android.os.Build
//import android.view.MotionEvent
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//import androidx.annotation.RequiresApi
//import androidx.graphics.lowlatency.BufferInfo
//import androidx.graphics.lowlatency.GLFrontBufferedRenderer
//import androidx.graphics.opengl.egl.EGLManager
//import androidx.input.motionprediction.MotionEventPredictor
//
//@RequiresApi(Build.VERSION_CODES.Q)
//class CustomGLView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
//
//    // 예시로 DATA_TYPE을 FloatArray로 설정
//    private val frontBufferRenderer: GLFrontBufferedRenderer<FloatArray>
//    private val motionEventPredictor: MotionEventPredictor = MotionEventPredictor.newInstance(this)
//
//    init {
//        // SurfaceHolder 설정
//        holder.addCallback(this)
//
//        // frontBufferRenderer 초기화
//        frontBufferRenderer = GLFrontBufferedRenderer(this, object : GLFrontBufferedRenderer.Callback<FloatArray> {
//            override fun onDrawFrontBufferedLayer(
//                eglManager: EGLManager,
//                bufferWidth: Int,
//                bufferHeight: Int,
//                bufferInfo: BufferInfo,
//                transform: FloatArray,
//                param: FloatArray
//            ) {
//                // OpenGL을 통한 프론트 버퍼에 그리기
//            }
//
//            override fun onDrawDoubleBufferedLayer(
//                eglManager: EGLManager,
//                bufferWidth: Int,
//                bufferHeight: Int,
//                transform: FloatArray,
//                params: Collection<FloatArray>
//            ) {
//                // OpenGL을 통한 더블 버퍼에 그리기
//            }
//        })
//
//        // 터치 이벤트를 위한 리스너 설정
//        setOnTouchListener { _, event ->
//            // MotionEvent 기록 및 예측
//            motionEventPredictor.record(event)
//            val predictedEvent = motionEventPredictor.predict() ?: event
//            handleTouchEvent(predictedEvent)
//            true
//        }
//    }
//
//    // MotionEvent를 FloatArray로 변환하는 함수
//    private fun MotionEvent.toFloatArray(): FloatArray {
//        return floatArrayOf(this.x, this.y, this.pressure)
//    }
//
//    private fun handleTouchEvent(event: MotionEvent) {
//        val dataPoint = event.toFloatArray()
//
//        when (event.action) {
//            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
//                // 프론트 버퍼 레이어에 직접 렌더링
//                frontBufferRenderer.renderFrontBufferedLayer(dataPoint)
//            }
//            MotionEvent.ACTION_UP -> {
//                // 그리기 완료 시, 더블 버퍼 레이어로 커밋
//                frontBufferRenderer.commit()
//            }
//        }
//    }
//
//    override fun surfaceCreated(holder: SurfaceHolder) {
//        // 필요 시 초기화 코드 작성
//    }
//
//    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
//        // SurfaceView가 변경될 때 처리
//    }
//
//    override fun surfaceDestroyed(holder: SurfaceHolder) {
//        // 리소스 해제
//        frontBufferRenderer.release(true)
////        motionEventPredictor
//    }
//}
