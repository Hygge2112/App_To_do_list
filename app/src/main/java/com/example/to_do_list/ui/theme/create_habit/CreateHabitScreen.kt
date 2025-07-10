package com.example.to_do_list.ui.theme.create_habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow // Thêm import này
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.to_do_list.ui.theme.CardDarkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tạo thói quen", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HabitNameAndIconSection()
                Spacer(modifier = Modifier.height(24.dp))
                ColorPickerSection()
                Spacer(modifier = Modifier.height(32.dp))
                RepetitionSection()
                Spacer(modifier = Modifier.height(32.dp))
                EndDateSection()
                Spacer(modifier = Modifier.height(24.dp))
                OtherOptionsSection()
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HabitNameAndIconSection() {
    var habitName by remember { mutableStateOf("") }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.SportsEsports, // Icon ví dụ
            contentDescription = "Habit Icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(CardDarkBackground)
                .clickable { /* TODO: Mở trang chọn icon */ }
                .padding(16.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        BasicTextField(
            value = habitName,
            onValueChange = { habitName = it },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (habitName.isEmpty()) {
                        Text(
                            text = "Nhập tên thói quen...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 20.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun ColorPickerSection() {
    val colors = listOf(
        Color(0xFF4CAF50), // Green
        Color(0xFF2196F3), // Blue
        Color(0xFF9E9E9E), // Grey
        Color(0xFFF44336)  // Red
    )
    var selectedColorIndex by remember { mutableStateOf(0) }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(colors.size) { index ->
            val color = colors[index]
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { selectedColorIndex = index }
                    .border(
                        width = 2.dp,
                        color = if (selectedColorIndex == index) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedColorIndex == index) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepetitionSection() {
    var selectedChip by remember { mutableStateOf("hàng tuần") }
    val days = listOf("S", "M", "T", "W", "T", "F", "S")
    val selectedDays = remember { mutableStateListOf("S", "M", "W") } // Ví dụ

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Khoảng thời gian và sự lặp lại", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        // Chips Hàng ngày/tuần/tháng
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Hàng ngày", "hàng tuần", "hàng tháng").forEach {
                FilterChip(
                    selected = selectedChip == it,
                    onClick = { selectedChip = it },
                    label = { Text(it) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Các ngày trong tuần
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            days.forEach { day ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (day in selectedDays) MaterialTheme.colorScheme.primary else CardDarkBackground)
                        .clickable {
                            if (day in selectedDays) selectedDays.remove(day) else selectedDays.add(day)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(day, color = if (day in selectedDays) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun EndDateSection() {
    var isEndDateEnabled by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("End Date", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Switch(checked = isEndDateEnabled, onCheckedChange = { isEndDateEnabled = it })
    }
}

@Composable
fun OtherOptionsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDarkBackground)
            .clickable { /* TODO: Mở menu tùy chọn */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Add, contentDescription = "Tùy chọn khác")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Tùy chọn khác", modifier = Modifier.weight(1f))
        Icon(Icons.Default.ExpandMore, contentDescription = "Mở rộng")
    }
}