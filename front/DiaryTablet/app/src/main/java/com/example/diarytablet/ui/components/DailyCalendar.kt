package com.example.diarytablet.ui.components
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.DeepPastelNavy
import com.example.diarytablet.ui.theme.GrayText
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.ui.theme.SkyBlue
import com.example.diarytablet.ui.theme.myFontFamily
import com.example.diarytablet.utils.playButtonSound
import com.example.diarytablet.viewmodel.LogViewModel
import java.time.LocalDateTime
import java.util.*

@Composable
fun DailyCalendar(
    viewModel: LogViewModel,
    onDateCellClick: (Int) -> Unit
) {

    val year by viewModel.year.collectAsState()
    val month by viewModel.month.collectAsState()
    val diaryList by viewModel.diaryList.observeAsState()

    LaunchedEffect(Unit) {
        if (diaryList?.body().isNullOrEmpty()) {
            viewModel.fetchDiaryList()
        }
    }

    val monthYearText = "${year}년 ${month + 1}월"
    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")

    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val diaryDatesSet = diaryList?.body()?.map { it.createdAt.toLocalDate() }?.toSet() ?: emptySet()

    var day = 1
    val totalCells = startDayOfWeek + daysInMonth
    val rows = (totalCells + 6) / 7
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 60.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            IconButton(onClick = { navigateToPreviousMonth(year, month) { updatedYear, updatedMonth ->
                viewModel.updateYearMonth(updatedYear, updatedMonth)
            } }) {
                Image(painter = painterResource(R.drawable.calender_back),
                    contentDescription = "Previous Month",
                    modifier = Modifier.size(60.dp, 60.dp))
            }

            Text(
                text = monthYearText,
                style = MyTypography.bodyLarge,
                color = GrayText,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

            IconButton(onClick = {
                navigateToNextMonth(year, month) { updatedYear, updatedMonth ->
                    viewModel.updateYearMonth(updatedYear, updatedMonth)
                } }) {
                Image(painter = painterResource(R.drawable.calender_next),
                    contentDescription = "Previous Month",
                    modifier = Modifier.size(60.dp, 60.dp))
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.size(48.dp),
                   style = MyTypography.bodyLarge,
                    color = DeepPastelNavy
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Column {
            for (i in 0 until rows) {
                if (i > 0) {
                    Spacer(modifier = Modifier.height(3.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (j in 0..6) {
                        if (i == 0 && j < startDayOfWeek || day > daysInMonth) {
                            Spacer(modifier = Modifier.size(48.dp))
                        } else {
                            val localDate = LocalDateTime.of(year, month + 1, day, 0, 0).toLocalDate()
                            val isDiaryDate = diaryDatesSet.contains(localDate)
                            val diaryId = diaryList?.body()?.firstOrNull { it.createdAt.toLocalDate() == localDate }?.id
                            DateCell(
                                date = day,
                                isDiaryDate = isDiaryDate,
                                diaryId = diaryId ?: -1, // diaryId가 null인 경우 빈 문자열을 전달
                                onClick = {
                                    if (isDiaryDate && diaryId != null) {
                                        playButtonSound(context, R.raw.all_button )
                                        onDateCellClick(diaryId) // 클릭 시 diaryId 전달
                                    }
                                }
                            )
                            day++

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateCell(
    date: Int,
    isDiaryDate: Boolean,
    diaryId: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                if (isDiaryDate) SkyBlue else Color.Transparent,
                shape = CircleShape
            )
            .clickable {
                onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$date",
            fontFamily = myFontFamily,
            fontSize = 26.sp,
            fontWeight = if (isDiaryDate) FontWeight.Bold else FontWeight.Normal,
            color = if (isDiaryDate) Color.White else GrayText
        )
    }
}

fun navigateToPreviousMonth(currentYear: Int, currentMonth: Int, updateMonth: (Int, Int) -> Unit) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        add(Calendar.MONTH, -1)
    }
    updateMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
}

fun navigateToNextMonth(currentYear: Int, currentMonth: Int, updateMonth: (Int, Int) -> Unit) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        add(Calendar.MONTH, 1)
    }
    updateMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
}

fun Date.isSameDay(other: Date): Boolean {
    val calendar1 = Calendar.getInstance().apply { time = this@isSameDay }
    val calendar2 = Calendar.getInstance().apply { time = other }
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
}