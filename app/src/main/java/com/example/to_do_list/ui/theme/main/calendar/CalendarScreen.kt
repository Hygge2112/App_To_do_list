package com.example.to_do_list.ui.theme.main.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.to_do_list.data.Habit
import com.example.to_do_list.data.HabitUiState
import com.example.to_do_list.data.HabitViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen() {
    // === TRẠNG THÁI (STATE) ===
    // 1. Lấy dữ liệu từ HabitViewModel
    val viewModel: HabitViewModel = viewModel()
    val habitUiState by viewModel.uiState.collectAsState()

    // 2. Quản lý tháng và ngày đang được chọn
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Header Lịch ---
        CalendarHeader(
            month = currentMonth,
            onPrevMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
        )
        MonthHeader()

        // --- Xử lý các trạng thái của UI (Loading, Error, Success) ---
        when (val state = habitUiState) {
            is HabitUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is HabitUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Lỗi: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is HabitUiState.Success -> {
                // 3. Nhóm các thói quen theo ngày để dễ dàng tra cứu và hiển thị
                val habitsByDate = remember(state.habits) {
                    groupHabitsByDate(state.habits)
                }

                // --- Lưới Lịch ---
                CalendarGrid(
                    yearMonth = currentMonth,
                    habitsByDate = habitsByDate,
                    selectedDate = selectedDate,
                    onDateSelected = { date -> selectedDate = date }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Danh sách công việc cho ngày đã chọn ---
                TaskList(
                    selectedDate = selectedDate,
                    habits = habitsByDate[selectedDate] ?: emptyList()
                )
            }
        }
    }
}

/**
 * Nhóm danh sách các thói quen theo từng ngày dựa trên `repetitionDates`.
 * Điều này giúp tăng hiệu suất bằng cách không phải lặp qua toàn bộ danh sách thói quen cho mỗi ô ngày.
 * @param habits Danh sách tất cả các thói quen từ ViewModel.
 * @return Một Map với khóa là `LocalDate` và giá trị là danh sách các thói quen vào ngày đó.
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun groupHabitsByDate(habits: List<Habit>): Map<LocalDate, List<Habit>> {
    val habitsByDate = mutableMapOf<LocalDate, MutableList<Habit>>()
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    habits.forEach { habit ->
        habit.repetitionDates.forEach { dateString ->
            try {
                val date = LocalDate.parse(dateString, formatter)
                habitsByDate.getOrPut(date) { mutableListOf() }.add(habit)
            } catch (e: Exception) {
                // Bỏ qua nếu định dạng ngày bị lỗi
            }
        }
    }
    return habitsByDate
}

// Composable hiển thị danh sách các công việc (đã cập nhật để dùng `Habit`)
@Composable
fun TaskList(selectedDate: LocalDate, habits: List<Habit>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Công việc ngày ${selectedDate.dayOfMonth}/${selectedDate.monthValue}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (habits.isEmpty()) {
            Text("Không có công việc nào cho ngày này.")
        } else {
            LazyColumn {
                items(habits) { habit ->
                    HabitTaskItem(habit)
                }
            }
        }
    }
}

// Composable cho một mục công việc trong danh sách (đã cập nhật để dùng `Habit`)
@Composable
fun HabitTaskItem(habit: Habit) {
    val habitColor = try {
        Color(android.graphics.Color.parseColor(habit.color))
    } catch (e: IllegalArgumentException) {
        MaterialTheme.colorScheme.primary // Màu mặc định nếu có lỗi
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(habitColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = habit.name, style = MaterialTheme.typography.bodyLarge)
    }
}

// --- Các Composable của Lịch (đã cập nhật) ---

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    habitsByDate: Map<LocalDate, List<Habit>>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstOfMonth = yearMonth.atDay(1)
    val dayOfWeekOffset = (firstOfMonth.dayOfWeek.value % 7) // CN là 0, T2 là 1..

    val calendarDays = mutableListOf<LocalDate?>()
    for (i in 0 until dayOfWeekOffset) {
        calendarDays.add(null) // Thêm các ô trống cho đầu tháng
    }
    for (day in 1..daysInMonth) {
        calendarDays.add(yearMonth.atDay(day))
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.padding(horizontal = 4.dp).heightIn(max = 350.dp),
        userScrollEnabled = false
    ) {
        items(calendarDays) { date ->
            if (date != null) {
                DayCell(
                    date = date,
                    isSelected = date == selectedDate,
                    habits = habitsByDate[date] ?: emptyList(), // Lấy danh sách thói quen cho ngày này
                    onClick = { onDateSelected(date) }
                )
            } else {
                Box(modifier = Modifier.aspectRatio(1f)) // Ô trống
            }
        }
    }
}

// Composable cho từng ô ngày trong lịch (đã cập nhật)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    habits: List<Habit>, // Nhận danh sách thói quen cho ngày này
    onClick: () -> Unit
) {
    val isSunday = date.dayOfWeek == DayOfWeek.SUNDAY
    val isToday = date == LocalDate.now()

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .border(
                width = if (isToday) 1.5.dp else 0.dp,
                color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = if (isSunday) Color(0xFFF97316) else MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
            )
            // Hiển thị các dấu chấm nếu có thói quen
            if (habits.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Lấy tối đa 3 màu sắc khác nhau từ các thói quen để làm dấu chấm
                    habits.map { it.color }.distinct().take(3).forEach { colorString ->
                        val dotColor = try {
                            Color(android.graphics.Color.parseColor(colorString))
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary // Màu mặc định nếu lỗi
                        }
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                }
            }
        }
    }
}

// CalendarHeader và MonthHeader không thay đổi
@Composable
fun CalendarHeader(month: YearMonth, onPrevMonth: () -> Unit, onNextMonth: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPrevMonth) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Tháng trước")
        }
        Text(
            text = "tháng ${month.monthValue}, ${month.year}",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Tháng sau")
        }
    }
}

@Composable
fun MonthHeader() {
    val daysOfWeek = listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        for (dayName in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (dayName == "CN") Color(0xFFF97316) else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}