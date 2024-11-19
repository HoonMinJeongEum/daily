package com.example.diarytablet.ui.components.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import com.example.diarytablet.R
import com.example.diarytablet.viewmodel.QuizViewModel


@Composable
fun ToggleMicButton(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier,
    enable: Boolean
) {
    val isMicMuted by viewModel.isMicMuted.observeAsState(true)

    Image(
        painter = painterResource(id = if (isMicMuted) R.drawable.quiz_mic_muted else R.drawable.quiz_mic_unmuted),
        contentDescription = if (isMicMuted) "Muted" else "Unmuted",
        modifier = modifier
            .clickable(
                enabled = enable,
                onClick = { viewModel.toggleMicMute() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .alpha(if (enable) 1f else 0.5f)
    )
}