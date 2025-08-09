package com.example.to_do_list.ui.theme.main.calendar

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
import androidx.compose.material.icons.filled.Add
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.random.Random

// Dữ liệu cho một sự kiện/nhiệm vụ
data class Event(val id: Int, val date: LocalDate, val title: String, val color: Color)

@Composable
fun CalendarScreen() {
    // === TRẠNG THÁI (STATE) ===
    // 1. Quản lý tháng đang hiển thị
    val currentMonth = remember { mutableStateOf(YearMonth.of(2025, 8)) }
    // 2. Quản lý ngày đang được chọn, mặc định là ngày hôm nay
    var selectedDate by remember { mutableStateOf(LocalDate.of(2025, 8, 8)) }
    // 3. Quản lý danh sách các sự kiện. Dùng mutableStateListOf để giao diện tự cập nhật khi thêm/xóa.
    val events = remember { mutableStateListOf<Event>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Giao diện Lịch ---
        CalendarHeader(
            month = currentMonth.value,
            onPrevMonth = { currentMonth.value = currentMonth.value.minusMonths(1) },
            onNextMonth = { currentMonth.value = currentMonth.value.plusMonths(1) }
        )
        MonthHeader()
        CalendarGrid(
            yearMonth = currentMonth.value,
            events = events,
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date // Cập nhật ngày được chọn khi người dùng nhấn vào
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Giao diện Danh sách công việc ---
        TaskList(
            selectedDate = selectedDate,
            events = events.filter { it.date == selectedDate } // Lọc công việc theo ngày đã chọn
        )

        // --- Nút Thêm công việc (Ví dụ) ---
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            FloatingActionButton(
                onClick = {
                    // Thêm một sự kiện mới vào danh sách cho ngày đang được chọn
                    val newEvent = Event(
                        id = events.size + 1,
                        date = selectedDate,
                        title = "Công việc mới ${Random.nextInt(1, 100)}",
                        color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
                    )
                    events.add(newEvent)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm công việc")
            }
        }
    }
}

// Composable cho danh sách công việc
@Composable
fun TaskList(selectedDate: LocalDate, events: List<Event>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Công việc ngày ${selectedDate.dayOfMonth}/${selectedDate.monthValue}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (events.isEmpty()) {
            Text("Không có công việc nào cho ngày này.")
        } else {
            LazyColumn {
                items(events) { event ->
                    TaskItem(event)
                }
            }
        }
    }
}

// Composable cho một mục công việc
@Composable
fun TaskItem(event: Event) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(event.color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = event.title, style = MaterialTheme.typography.bodyLarge)
    }
}


// --- Các Composable của Lịch (đã cập nhật) ---

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    events: List<Event>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit // Callback để thông báo ngày được chọn
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstOfMonth = yearMonth.atDay(1)
    val dayOfWeekOffset = (firstOfMonth.dayOfWeek.value % 7)

    val calendarDays = mutableListOf<LocalDate?>()
    for (i in 0 until dayOfWeekOffset) {
        calendarDays.add(null)
    }
    for (day in 1..daysInMonth) {
        calendarDays.add(yearMonth.atDay(day))
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.padding(horizontal = 4.dp),
        userScrollEnabled = false // Vô hiệu hóa cuộn cho lưới
    ) {
        items(calendarDays) { date ->
            if (date != null) {
                DayCell(
                    date = date,
                    isSelected = date == selectedDate,
                    events = events.filter { it.date == date },
                    onClick = { onDateSelected(date) }
                )
            } else {
                Box(modifier = Modifier.aspectRatio(1f))
            }
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    events: List<Event>,
    onClick: () -> Unit
) {
    val isSunday = date.dayOfWeek == DayOfWeek.SUNDAY

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick) // Thêm hành động nhấn vào
            .background(
                if (isSelected) Color(0xFFC8E6C9) else Color(0xFFE0F2F1)
            )
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected) Color(0xFF14B8A6) else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = if (isSunday) Color(0xFFF97316) else Color.Black,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            // Chỉ hiển thị dấu chấm nếu có sự kiện
            if (events.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    events.take(3).forEach { event ->
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(event.color)
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
            text = "tháng ${month.monthValue} ${month.year}",
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