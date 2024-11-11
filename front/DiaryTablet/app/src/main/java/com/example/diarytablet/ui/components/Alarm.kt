package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R

@Composable
fun AlarmButton(
    isAlarmOn: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val alarmIcon = if (isAlarmOn) R.drawable.alarm_on else R.drawable.alarm_off

    Image(
        painter = painterResource(id = alarmIcon),
        contentDescription = "Alarm Button",
        modifier = modifier
            .size(75.dp)
            .clickable(onClick = onClick)
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
