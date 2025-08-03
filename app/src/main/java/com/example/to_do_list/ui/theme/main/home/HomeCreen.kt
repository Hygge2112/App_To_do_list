package com.example.to_do_list.ui.theme.main.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.to_do_list.data.Habit
import com.example.to_do_list.data.HabitViewModel
import com.example.to_do_list.navigation.Routes
import com.example.to_do_list.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HabitViewModel = viewModel()
    val habits by viewModel.habits.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(Unit) {
        val auth = Firebase.auth
        if (auth.currentUser == null) {
            try {
                auth.signInAnonymously().await()
                Log.d("HabitApp", "Đăng nhập ẩn danh thành công: ${auth.currentUser?.uid}")
                viewModel.loadHabitsForDate(selectedDate)
            } catch (e: Exception) {
                Log.e("HabitApp", "Lỗi đăng nhập ẩn danh", e)
            }
        } else {
            Log.d("HabitApp", "Người dùng đã đăng nhập: ${auth.currentUser?.uid}")
        }
    }

    LaunchedEffect(selectedDate) {
        viewModel.loadHabitsForDate(selectedDate)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Hygge To-do List",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }
        item {
            StatsCards(navController = navController)
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            WeekCalendar(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            FilterChips()
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (habits.isEmpty()) {
            item {
                EmptyState()
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                HabitItem(
                    habit = habit,
                    onCompletedToggle = {
                        viewModel.toggleHabitCompleted(habit.id, !habit.isCompleted)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitItem(habit: Habit, onCompletedToggle: () -> Unit) {
    val cardColor = try {
        Color(android.graphics.Color.parseColor(habit.color))
    } catch (e: IllegalArgumentException) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = "Habit Icon",
                tint = cardColor,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(cardColor.copy(alpha = 0.2f))
                    .padding(12.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(habit.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = habit.reminderTime ?: "No Time Selected",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Checkbox(
                checked = habit.isCompleted,
                onCheckedChange = { onCompletedToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = cardColor,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsCards(navController: NavController) {
    var userName by remember { mutableStateOf("Hygge") }
    var showEditNameDialog by remember { mutableStateOf(false) }

    if (showEditNameDialog) {
        EditNameDialog(
            currentName = userName,
            onNameChange = { newName -> userName = newName },
            onDismiss = { showEditNameDialog = false }
        )
    }

    val currentDateTextInEnglish = remember {
        val now = LocalDate.now()
        val dayOfMonth = now.dayOfMonth
        val daySuffix = getDayOfMonthSuffix(dayOfMonth)
        val dayOfWeek = now.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        val month = now.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        "$dayOfWeek ${dayOfMonth}${daySuffix}, $month"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1.8f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDarkBackground)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(userName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(currentDateTextInEnglish, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { showEditNameDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Name",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("0", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary)
                    Text("Thói quen", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { navController.navigate(Routes.ADD_HABIT) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm thói quen",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditNameDialog(
    currentName: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đổi tên của bạn") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Tên mới") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onNameChange(text)
                    }
                    onDismiss()
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

data class CalendarDate(val dayOfMonth: Int, val dayOfWeek: String, val date: LocalDate)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekCalendar(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val dates = (0..365).map {
        val date = today.plusDays(it.toLong())
        CalendarDate(
            dayOfMonth = date.dayOfMonth,
            dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("vi")).replaceFirstChar { it.uppercase() },
            date = date
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            state = lazyListState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 120.dp)
        ) {
            itemsIndexed(items = dates, key = { _, date -> date.date }) { index, date ->
                DateItem(
                    date = date,
                    isSelected = date.date == selectedDate,
                    onDateClick = {
                        onDateSelected(it)
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(index = (index - 2).coerceAtLeast(0))
                        }
                    }
                )
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
            visible = selectedDate != today,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Row(
                modifier = Modifier
                    .height(48.dp)
                    .clip(CircleShape)
                    .background(OffWhite)
                    .clickable {
                        onDateSelected(today)
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay về hôm nay",
                    tint = DarkBackground
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Hôm nay",
                    color = DarkBackground,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun DateItem(date: CalendarDate, isSelected: Boolean, onDateClick: (LocalDate) -> Unit) {
    // ---- DÒNG NÀY ĐÃ ĐƯỢC SỬA ----
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else CardDarkBackground

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onDateClick(date.date) }
            .padding(vertical = 16.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = date.dayOfMonth.toString(), fontWeight = FontWeight.Bold, color = contentColor, fontSize = 20.sp)
        Text(text = date.dayOfWeek, color = contentColor, fontSize = 14.sp)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = true, onClick = { }, label = { Text("Tất cả - 0") })
        FilterChip(selected = false, onClick = { }, label = { Text("Thói quen - 0") })
        FilterChip(selected = false, onClick = { }, label = { Text("Nhiệm vụ - 0") })
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Inventory2,
            contentDescription = "Empty",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Text(
            text = "Không có nhiệm vụ nào",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun getDayOfMonthSuffix(n: Int): String {
    if (n in 11..13) {
        return "th"
    }
    return when (n % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}