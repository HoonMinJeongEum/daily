package com.example.diaryApp.ui.components.quiz

import android.view.LayoutInflater
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.diaryApp.MainActivity
import com.example.diaryApp.databinding.ActivityMainBinding
import com.example.diaryApp.utils.openvidu.CustomWebSocket
import com.example.diaryApp.utils.openvidu.LocalParticipant
import com.example.diaryApp.utils.openvidu.Session
import com.example.diaryApp.viewmodel.QuizViewModel
import org.webrtc.EglBase
import org.webrtc.RendererCommon

@Composable
fun Video(
    modifier: Modifier,
    viewModel: QuizViewModel
) {
    val context = LocalContext.current
    val binding = remember { ActivityMainBinding.inflate(LayoutInflater.from(context)) }
    val activity = context as? MainActivity
    val rootEglBase = remember { EglBase.create() }
    val remoteMediaStream by viewModel.remoteMediaStream.observeAsState()
    val leaveSessionTriggered by viewModel.leaveSessionTriggered.observeAsState()
    val sessionId by viewModel.sessionId.observeAsState()
    val token by viewModel.token.observeAsState()

    AndroidView(factory = { context ->
        binding.localGlSurfaceView.apply {
            init(rootEglBase.eglBaseContext, null)
            setMirror(true)
            setEnableHardwareScaler(true)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            setZOrderMediaOverlay(true)
            visibility = View.INVISIBLE
        }
        binding.root
    })

    LaunchedEffect(remoteMediaStream) {
        remoteMediaStream?.let { stream ->
            val videoTrack = stream.videoTracks[0]
            videoTrack.addSink(binding.localGlSurfaceView)

            // UI 업데이트는 메인 스레드에서 수행
            binding.localGlSurfaceView.visibility = View.VISIBLE
        }
    }
    LaunchedEffect(leaveSessionTriggered) {
        if (leaveSessionTriggered == true) {
            // UI 요소 상태 초기화
            binding.localGlSurfaceView.apply {
                clearImage()
                release()
                visibility = View.INVISIBLE
            }
            // ViewModel에서 상태 초기화
            viewModel.resetLeaveSessionTrigger()
        }
    }
    LaunchedEffect(token) {
        if (token != null && activity != null) {
            val session = Session(sessionId ?: "", token!!, binding.viewsContainer, activity, viewModel, rootEglBase)
            viewModel.session = session
            val localParticipant = LocalParticipant("participantName", session, activity.applicationContext, binding.localGlSurfaceView)
            localParticipant.startCamera()

            // WebSocket 초기화
            val webSocket = CustomWebSocket(session, activity, viewModel)
            webSocket.execute()
            session.setWebSocket(webSocket)
        }
    }
}