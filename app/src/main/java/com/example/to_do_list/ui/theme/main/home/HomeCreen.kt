package com.example.to_do_list.ui.theme.main.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.to_do_list.data.Habit
import com.example.to_do_list.data.HabitViewModel
import com.example.to_do_list.navigation.Routes
import com.example.to_do_list.ui.theme.*
// THÊM MỚI: Import HabitIconProvider để sử dụng
import com.example.to_do_list.ui.theme.create_habit.HabitIconProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HabitViewModel = viewModel()
    val habits by viewModel.habits.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val auth = Firebase.auth

    var showDeleteDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }

    var showCongratsDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && habitToDelete != null) {
        DeleteConfirmationDialog(
            habitName = habitToDelete!!.name,
            onConfirm = {
                viewModel.deleteHabit(habitToDelete!!.id)
                showDeleteDialog = false
                habitToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                habitToDelete = null
            }
        )
    }

    if (showCongratsDialog) {
        CongratulationsDialog(onDismiss = { showCongratsDialog = false })
    }

    LaunchedEffect(selectedDate, auth.currentUser) {
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
            StatsCards(navController = navController, habitCount = habits.size)
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
            FilterChips(habits.size)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (habits.isEmpty()) {
            item {
                EmptyState()
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                SwipeToDeleteContainer(
                    key = habit.id,
                    onDelete = {
                        habitToDelete = habit
                        showDeleteDialog = true
                    }
                ) {
                    HabitItem(
                        habit = habit,
                        selectedDate = selectedDate,
                        onCompletedToggle = { isNowCompleted ->
                            viewModel.toggleHabitCompletionForDate(habit.id, selectedDate, isNowCompleted)
                            if (isNowCompleted) {
                                showCongratsDialog = true
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// --- HÀM `HabitItem` ĐÃ ĐƯỢC CẬP NHẬT ---
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitItem(
    habit: Habit,
    selectedDate: LocalDate,
    onCompletedToggle: (isCompleted: Boolean) -> Unit
) {
    val habitColor = try {
        Color(android.graphics.Color.parseColor(habit.color))
    } catch (e: IllegalArgumentException) {
        MaterialTheme.colorScheme.primary
    }

    // THAY ĐỔI: Lấy icon chính xác từ provider dựa trên `iconName` đã lưu
    val habitIcon = HabitIconProvider.getIcon(habit.iconName, null)

    val isCompletedForDay = habit.completedDates.contains(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))

    val cardContainerColor = if (isCompletedForDay) {
        lerp(habitColor, MaterialTheme.colorScheme.surface, 0.5f)
    } else {
        habitColor
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = habitIcon, // Sử dụng icon đã được lấy ra
                contentDescription = "Habit Icon",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    textDecoration = if (isCompletedForDay) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = habit.reminderTime ?: "No Time Selected",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    textDecoration = if (isCompletedForDay) TextDecoration.LineThrough else TextDecoration.None
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (isCompletedForDay) Color.White else Color.Transparent,
                        shape = CircleShape
                    )
                    .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
                    .clickable { onCompletedToggle(!isCompletedForDay) },
                contentAlignment = Alignment.Center
            ) {
                if (isCompletedForDay) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = habitColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


// --- CÁC HÀM KHÁC KHÔNG THAY ĐỔI ---

@Composable
fun CongratulationsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("🎉", fontSize = 48.sp)
                Text(
                    text = "Chúc mừng",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Của bạn Habit đã xong!!",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(50)
            ) {
                Text("Hiểu rồi", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun SwipeToDeleteContainer(
    key: Any,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetX by remember(key) { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    val deleteButtonWidth = 80.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(deleteButtonWidth)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.clickable(onClick = onDelete),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = AccentRed
                )
                Text("Delete", color = AccentRed, fontSize = 12.sp)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                val deleteWidthPx = deleteButtonWidth.toPx()
                                if (offsetX < -deleteWidthPx / 2) {
                                    offsetX = -deleteWidthPx
                                } else {
                                    offsetX = 0f
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            val deleteWidthPx = deleteButtonWidth.toPx()
                            val newOffsetX = (offsetX + dragAmount).coerceIn(-deleteWidthPx, 0f)
                            offsetX = newOffsetX
                            change.consume()
                        }
                    )
                }
        ) {
            content()
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    habitName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Xóa thói quen", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        },
        text = {
            Text(
                text = "Bạn có chắc chắn muốn xóa thói quen?",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                shape = RoundedCornerShape(50)
            ) {
                Text("Đúng", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text("Không", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(habitCount: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = true, onClick = { }, label = { Text("Tất cả - $habitCount") })
        FilterChip(selected = false, onClick = { }, label = { Text("Thói quen - $habitCount") })
        FilterChip(selected = false, onClick = { }, label = { Text("Nhiệm vụ - 0") })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsCards(navController: NavController, habitCount: Int) {
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
        val dayOfWeek = now.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        val dayOfMonth = now.dayOfMonth
        val daySuffix = getDayOfMonthSuffix(dayOfMonth)
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
                    Text(habitCount.toString(), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary)
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
    val todayIndex = 365
    val scrollIndex = (todayIndex - 2).coerceAtLeast(0)

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val dates = (-365..365).map {
        val date = today.plusDays(it.toLong())
        CalendarDate(
            dayOfMonth = date.dayOfMonth,
            dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("vi")).replaceFirstChar { it.uppercase() },
            date = date
        )
    }

    LaunchedEffect(Unit) {
        lazyListState.scrollToItem(scrollIndex)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            state = lazyListState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
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
                            lazyListState.animateScrollToItem(index = scrollIndex)
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