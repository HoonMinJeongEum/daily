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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.R
import com.example.diaryApp.domain.dto.response.diary.DiaryForList
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.GrayText
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.SkyBlue
import com.example.diaryApp.viewmodel.QuizViewModel
import java.time.LocalDateTime
import java.util.*

@Composable
fun DailyCalendar(
    screenWidth : Dp,
    viewModel: DiaryViewModel,
    navController: NavController,
    childName: String
) {

    val year by viewModel.year.collectAsState()
    val month by viewModel.month.collectAsState()
    val diaryList by viewModel.diaryList.observeAsState()

    LaunchedEffect(Unit) {
//        if (diaryList?.body().isNullOrEmpty()) {
        viewModel.clearDiaryDetail()
        viewModel.fetchDiaryList()
//        }
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(screenWidth * 0.05f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = screenWidth * 0.06f)
        ) {

            IconButton(onClick = { navigateToPreviousMonth(year, month) { updatedYear, updatedMonth ->
                viewModel.updateYearMonth(updatedYear, updatedMonth)
            } }) {
                Image(painter = painterResource(R.drawable.calender_back),
                    contentDescription = "Previous Month",
                    modifier = Modifier.size(screenWidth * 0.08f)
                )
            }

            Text(
                text = monthYearText,
                style = MyTypography.bodyMedium.copy(
                    fontSize = (screenWidth * 0.06f).value.sp
                ),
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
                    modifier = Modifier.size(screenWidth * 0.08f)
                )
            }
        }

        Spacer(modifier = Modifier.height(screenWidth * 0.06f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MyTypography.bodySmall.copy(
                        fontSize = (screenWidth * 0.05f).value.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.size(screenWidth * 0.12f)                )
            }
        }

        Spacer(modifier = Modifier.height(screenWidth * 0.02f))
        Column {
            for (i in 0 until rows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (j in 0..6) {
                        if (i == 0 && j < startDayOfWeek || day > daysInMonth) {
                            Spacer(modifier = Modifier.size(screenWidth * 0.1f))                        } else {
                            val date = calendar.apply { set(Calendar.DAY_OF_MONTH, day) }.time
                            val localDate = LocalDateTime.of(year, month + 1, day, 0, 0).toLocalDate()
                            val isDiaryDate = diaryDatesSet.contains(LocalDateTime.of(year, month + 1, day, 0, 0).toLocalDate())
                            val diaryId = diaryList?.body()?.firstOrNull { it.createdAt.toLocalDate() == localDate }?.id.toString()
                            DateCell(
                                screenWidth = screenWidth,
                                date = day,
                                isDiaryDate = isDiaryDate,
                                onClick = {
                                    if (isDiaryDate) {
                                        Log.d("diaryScreen", "Selected Date: $localDate")
                                        Log.d("diaryScreen", "Diary Id: $diaryId")
                                    }
                                },
                                diaryId = diaryId,
                                navController = navController,
                                childName = childName
                            )
                            day++
                        }
                    }
                }
                Spacer(modifier = Modifier.height(screenWidth * 0.02f)) // 줄 사이에 간격 추가

            }
        }
    }
}

@Composable
fun DateCell(
    screenWidth: Dp,
    date: Int,
    isDiaryDate: Boolean,
    diaryId: String,
    navController: NavController,
    onClick: () -> Unit,
    childName: String
) {
    Box(
        modifier = Modifier
            .size(screenWidth * 0.1f)
            .background(
                if (isDiaryDate) SkyBlue else Color.Transparent,
                shape = CircleShape
            )
            .clickable {
                onClick()
                if (isDiaryDate && diaryId != null) {
                    navController.navigate("diary/$diaryId/${childName}")
                } },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$date",
            style = MyTypography.bodySmall.copy(
                fontSize = (screenWidth * 0.05f).value.sp,
            ),
            fontWeight = if (isDiaryDate) FontWeight.Bold else FontWeight.Normal,
            color = if (isDiaryDate) Color.White else MaterialTheme.colorScheme.onSurface
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