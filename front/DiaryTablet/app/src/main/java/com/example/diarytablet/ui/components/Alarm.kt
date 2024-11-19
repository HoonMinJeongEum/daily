package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R
import com.example.diarytablet.utils.playButtonSound

@Composable
fun AlarmButton(
    isAlarmOn: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val alarmIcon = if (isAlarmOn) R.drawable.alarm_on else R.drawable.alarm_off
    val context = LocalContext.current

    Image(
        painter = painterResource(id = alarmIcon),
        contentDescription = "Alarm Button",
        modifier = modifier
            .size(75.dp)
            .clickable(
                onClick = {
                    onClick()
                    playButtonSound(context,R.raw.all_button )

                },
                indication = null, // 클릭 효과 제거
                interactionSource = remember { MutableInteractionSource() }
            )
    )
}

@Preview(showBackground = true)
@Composable
fun previewAlarmButton() {
    AlarmButton(
        isAlarmOn = false,
        onClick = {}
    )
}
