package com.example.to_do_list.ui.theme.create_habit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.to_do_list.data.Habit
import com.example.to_do_list.data.HabitViewModel
import com.example.to_do_list.navigation.Routes
import com.example.to_do_list.ui.theme.CardDarkBackground
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.compose.foundation.lazy.items as foundationLazyItems

// --- TRUNG TÂM QUẢN LÝ ICON ---
object HabitIconProvider {
    // Bản đồ chứa TẤT CẢ các icon có thể sử dụng, được định danh bằng một chuỗi duy nhất.
    private val iconMap: Map<String, ImageVector> = mapOf(
        // Icons cho Danh mục
        "Psychology" to Icons.Default.Psychology, "Work" to Icons.Default.Work, "Lock" to Icons.Default.Lock,
        "ShoppingCart" to Icons.Default.ShoppingCart, "AccountBalanceWallet" to Icons.Default.AccountBalanceWallet,
        "People" to Icons.Default.People, "Group" to Icons.Default.Group, "Event" to Icons.Default.Event,
        "Flight" to Icons.Default.Flight, "Receipt" to Icons.Default.Receipt, "School" to Icons.Default.School,
        "SelfImprovement" to Icons.Default.SelfImprovement, "FitnessCenter" to Icons.Default.FitnessCenter,
        "Spa" to Icons.Default.Spa, "EditNote" to Icons.Default.EditNote,

        // Icons cho các Gợi ý cụ thể
        "Task" to Icons.Default.Task, "Bedtime" to Icons.Default.Bedtime, "PriorityHigh" to Icons.Default.PriorityHigh,
        "FlightTakeoff" to Icons.Default.FlightTakeoff, "Description" to Icons.Default.Description,
        "Groups" to Icons.Default.Groups, "ContactPage" to Icons.Default.ContactPage, "ReceiptLong" to Icons.Default.ReceiptLong,
        "Folder" to Icons.Default.Folder, "Slideshow" to Icons.Default.Slideshow, "Email" to Icons.Default.Email,
        "StarRate" to Icons.Default.StarRate, "TrendingUp" to Icons.Default.TrendingUp, "Handshake" to Icons.Default.Handshake,
        "Weekend" to Icons.Default.Weekend, "MenuBook" to Icons.AutoMirrored.Filled.MenuBook, "Call" to Icons.Default.Call,
        "Restaurant" to Icons.Default.Restaurant, "Favorite" to Icons.Default.Favorite, "Book" to Icons.Default.Book,
        "Movie" to Icons.Default.Movie, "PhotoLibrary" to Icons.Default.PhotoLibrary, "Dashboard" to Icons.Default.Dashboard,
        "VideogameAsset" to Icons.Default.VideogameAsset, "VolunteerActivism" to Icons.Default.VolunteerActivism,
        "Mail" to Icons.Default.Mail, "CleaningServices" to Icons.Default.CleaningServices
        // Thêm các icon khác nếu cần
    )

    // Bản đồ ánh xạ từ Tên Danh mục (tiếng Việt) sang Định danh Icon (tiếng Anh)
    private val categoryToIconKeyMap: Map<String, String> = mapOf(
        "Tập trung" to "Psychology", "Công việc" to "Work", "Riêng tư" to "Lock", "Mua sắm" to "ShoppingCart",
        "Tài chính" to "AccountBalanceWallet", "Gia đình" to "People", "Xã hội" to "Group", "Cuộc hẹn" to "Event",
        "Du lịch" to "Flight", "Hóa đơn & Thanh toán" to "Receipt", "Học hỏi" to "School",
        "Tâm linh" to "SelfImprovement", "Sức khỏe & Thể hình" to "FitnessCenter", "Tự chăm sóc" to "Spa"
    )

    /**
     * Hàm chính để lấy icon.
     * Ưu tiên 1: Lấy theo tên icon cụ thể.
     * Ưu tiên 2: Nếu không có, lấy theo icon của danh mục.
     * Ưu tiên 3: Nếu vẫn không có, trả về icon mặc định.
     */
    fun getIcon(iconName: String?, categoryName: String?): ImageVector {
        iconName?.let { name -> iconMap[name]?.let { return it } }
        categoryName?.let { name -> categoryToIconKeyMap[name]?.let { key -> iconMap[key]?.let { return it } } }
        return Icons.Default.EditNote
    }

    // Hàm mới để lấy tên định danh của icon để lưu vào DB
    fun getIconName(iconName: String?, categoryName: String?): String {
        return iconName ?: categoryToIconKeyMap[categoryName] ?: "EditNote"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(navController: NavController, habitName: String?, categoryName: String?, iconName: String?) {
    val viewModel: HabitViewModel = viewModel()

    var habitNameState by remember { mutableStateOf(habitName ?: "") }
    val colors = listOf(Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFF9E9E9E), Color(0xFFF44336))
    var selectedColor by remember { mutableStateOf(colors.first()) }
    val selectedDates = remember { mutableStateListOf<LocalDate>() }
    val timePickerState = rememberTimePickerState(is24Hour = false)
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    val habitIcon = HabitIconProvider.getIcon(iconName, categoryName)

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
        bottomBar = {
            CreateHabitBottomButton(onClick = {
                if (habitNameState.isNotBlank()) {
                    val newHabit = Habit(
                        name = habitNameState,
                        color = "#${Integer.toHexString(selectedColor.toArgb()).substring(2).uppercase()}",
                        repetitionDates = selectedDates.map { it.format(DateTimeFormatter.ISO_LOCAL_DATE) },
                        reminderTime = selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
                        iconName = HabitIconProvider.getIconName(iconName, categoryName)
                    )
                    viewModel.addHabit(newHabit) {
                        navController.popBackStack(Routes.ADD_HABIT, inclusive = true)
                    }
                }
            })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            HabitNameAndIconSection(
                initialHabitName = habitName,
                icon = habitIcon,
                onNameChange = { habitNameState = it }
            )
            Spacer(modifier = Modifier.height(24.dp))
            ColorPickerSection(
                colors = colors,
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )
            Spacer(modifier = Modifier.height(32.dp))
            RepetitionSection(
                selectedDates = selectedDates,
                onDateSelected = { date ->
                    if (selectedDates.contains(date)) selectedDates.remove(date)
                    else selectedDates.add(date)
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
            ReminderSection(
                timePickerState = timePickerState,
                selectedTime = selectedTime,
                onTimeSelected = { selectedTime = it }
            )
        }
    }
}

@Composable
fun HabitNameAndIconSection(
    initialHabitName: String?,
    icon: ImageVector,
    onNameChange: (String) -> Unit
) {
    var habitName by remember { mutableStateOf(initialHabitName ?: "") }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
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
            onValueChange = {
                habitName = it
                onNameChange(it)
            },
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
                            "Nhập tên thói quen...",
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
fun CreateHabitBottomButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Tạo thói quen", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ColorPickerSection(colors: List<Color>, selectedColor: Color, onColorSelected: (Color) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        foundationLazyItems(colors) { color ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(color) }
                    .border(
                        width = 2.dp,
                        color = if (selectedColor == color) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedColor == color) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RepetitionSection(selectedDates: List<LocalDate>, onDateSelected: (LocalDate) -> Unit) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Lặp lại vào các ngày", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        MonthHeader(
            month = currentMonth,
            onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        MonthCalendarGrid(
            month = currentMonth,
            selectedDates = selectedDates,
            onDateClick = onDateSelected
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSection(timePickerState: TimePickerState, selectedTime: LocalTime?, onTimeSelected: (LocalTime?) -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = {
                onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                showTimePicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Time", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            selectedTime?.let {
                Text(
                    text = it.format(DateTimeFormatter.ofPattern("hh:mm a")),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Switch(
            checked = selectedTime != null,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    showTimePicker = true
                } else {
                    onTimeSelected(null)
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthHeader(month: YearMonth, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("vi"))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Tháng trước")
        }
        Text(
            text = month.format(formatter).replaceFirstChar { it.titlecase(Locale.getDefault()) },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Tháng sau")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthCalendarGrid(
    month: YearMonth,
    selectedDates: List<LocalDate>,
    onDateClick: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val daysInMonth = month.lengthOfMonth()
    val firstDayOfMonth = month.atDay(1).dayOfWeek.value % 7
    val days = (1..daysInMonth).toList()
    val weekDays = listOf("CN", "2", "3", "4", "5", "6", "7")

    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            weekDays.forEach { day ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(firstDayOfMonth) {
                Spacer(Modifier)
            }
            items(days) { day ->
                val date = month.atDay(day)
                val isEnabled = !date.isBefore(today)
                val isSelected = date in selectedDates

                val backgroundColor = when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> CardDarkBackground
                }
                val contentColor = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isEnabled -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .clickable(enabled = isEnabled) { onDateClick(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.toString(),
                        color = contentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String = "Chọn giờ",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .widthIn(max = 360.dp)
            .padding(24.dp),
        title = {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
                Box(Modifier.align(Alignment.CenterEnd)) {
                    toggle()
                }
            }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
