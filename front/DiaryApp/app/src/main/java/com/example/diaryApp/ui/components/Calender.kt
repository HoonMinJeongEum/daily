import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.Gray
import com.example.diaryApp.ui.theme.GrayText
import java.util.*

@Composable
fun DailyCalendar() {
    var year by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance().time) }

    val monthYearText = "${year}년 ${month + 1}월"
    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")

    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    var day = 1
    val totalCells = startDayOfWeek + daysInMonth
    val rows = (totalCells + 6) / 7

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(36.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom=24.dp)
        ) {

            IconButton(onClick = { navigateToPreviousMonth(year, month) { updatedYear, updatedMonth ->
                year = updatedYear
                month = updatedMonth
            } }) {
                Image(painter = painterResource(R.drawable.calender_back),
                    contentDescription = "Previous Month",
                    modifier = Modifier.size(30.dp, 30.dp))
            }

            Text(
                text = monthYearText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = GrayText,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

            IconButton(onClick = { navigateToNextMonth(year, month) { updatedYear, updatedMonth ->
                year = updatedYear
                month = updatedMonth
            } }) {
                Image(painter = painterResource(R.drawable.calender_next),
                    contentDescription = "Previous Month",
                    modifier = Modifier.size(30.dp, 30.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            for (i in 0 until rows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (j in 0..6) {
                        if (i == 0 && j < startDayOfWeek || day > daysInMonth) {
                            Spacer(modifier = Modifier.size(36.dp))
                        } else {
                            val date = calendar.apply { set(Calendar.DAY_OF_MONTH, day) }.time
                            DateCell(
                                date = day,
                                isSelected = selectedDate.isSameDay(date),
                                onClick = {
                                    selectedDate = date
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
fun DateCell(date: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                if (isSelected) DeepPastelNavy else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$date",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 18.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
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
