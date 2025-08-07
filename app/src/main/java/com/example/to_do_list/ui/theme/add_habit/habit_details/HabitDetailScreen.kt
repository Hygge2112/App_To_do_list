package com.example.to_do_list.ui.theme.add_habit.habit_details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.to_do_list.navigation.Routes
import com.example.to_do_list.ui.theme.create_habit.HabitIconProvider

// THAY ĐỔI 1: Data class giờ sẽ lưu tên định danh của icon
data class SuggestedHabit(
    val name: String,
    val iconName: String
)

// THAY ĐỔI 2: Cập nhật lại danh sách gợi ý để sử dụng tên định danh
private val focusSuggestions = listOf(
    SuggestedHabit("Học một kỹ năng mới", "School"),
    SuggestedHabit("Hoàn thành nhiệm vụ dự án", "Description"),
    SuggestedHabit("Ngủ 8 tiếng", "Bedtime"),
    SuggestedHabit("Đặt 3 nhiệm vụ ưu tiên mỗi ngày", "PriorityHigh"),
    SuggestedHabit("Thiền", "SelfImprovement"),
    SuggestedHabit("Bài tập", "FitnessCenter"),
    SuggestedHabit("Tham dự một sự kiện", "Event"),
    SuggestedHabit("Kế hoạch kỳ nghỉ", "Flight")
)
private val workSuggestions = listOf(
    SuggestedHabit("Hoàn thành báo cáo dự án", "Description"),
    SuggestedHabit("Tham dự cuộc họp nhóm", "Groups"),
    SuggestedHabit("Cập nhật sơ yếu lý lịch", "ContactPage"),
    SuggestedHabit("Gửi báo cáo chi phí", "ReceiptLong"),
    SuggestedHabit("Sắp xếp tập tin", "Folder"),
    SuggestedHabit("Chuẩn bị bài thuyết trình", "Slideshow"),
    SuggestedHabit("Kiểm tra email", "Email"),
    SuggestedHabit("Đánh giá hiệu suất của nhóm", "StarRate"),
    SuggestedHabit("Nghiên cứu xu hướng ngành", "TrendingUp"),
    SuggestedHabit("Hợp tác nhóm", "Handshake")
)
// ... (Các danh sách khác cũng cần được cập nhật tương tự nếu bạn muốn sử dụng chúng)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(navController: NavController, categoryName: String) {
    val suggestions = when (categoryName) {
        "Tập trung" -> focusSuggestions
        "Công việc" -> workSuggestions
        // Thêm các case khác ở đây
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(categoryName, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(suggestions) { suggestion ->
                SuggestedHabitItem(
                    suggestion = suggestion,
                    navController = navController,
                    categoryName = categoryName
                )
            }
        }
    }
}

@Composable
fun SuggestedHabitItem(
    suggestion: SuggestedHabit,
    navController: NavController,
    categoryName: String
) {
    // THAY ĐỔI 3: Lấy ImageVector từ provider để hiển thị
    val iconVector = HabitIconProvider.getIcon(suggestion.iconName, null)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF2A2A2A))
            .clickable {
                // THAY ĐỔI 4: Truyền cả 3 tham số khi điều hướng
                val route = "create_habit?habitName=${suggestion.name}&categoryName=${categoryName}&iconName=${suggestion.iconName}"
                navController.navigate(route)
            }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = iconVector, // Hiển thị icon đã được lấy ra
            contentDescription = suggestion.name,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .padding(12.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = suggestion.name,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
