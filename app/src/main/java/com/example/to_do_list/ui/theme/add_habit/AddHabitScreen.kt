package com.example.to_do_list.ui.theme.add_habit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.to_do_list.navigation.Routes
import com.example.to_do_list.ui.theme.CardDarkBackground

// --- NGUỒN QUẢN LÝ ICON TẬP TRUNG ---
object HabitIconProvider {
    val categories = listOf(
        "Tập trung", "Công việc", "Riêng tư", "Mua sắm", "Tài chính",
        "Gia đình", "Xã hội", "Cuộc hẹn", "Du lịch", "Hóa đơn & Thanh toán",
        "Học hỏi", "Tâm linh", "Sức khỏe & Thể hình", "Tự chăm sóc"
    )

    private val iconMap = mapOf(
        "Tập trung" to Icons.Default.Psychology,
        "Công việc" to Icons.Default.Work,
        "Riêng tư" to Icons.Default.Lock,
        "Mua sắm" to Icons.Default.ShoppingCart,
        "Tài chính" to Icons.Default.AccountBalanceWallet,
        "Gia đình" to Icons.Default.People,
        "Xã hội" to Icons.Default.Group,
        "Cuộc hẹn" to Icons.Default.Event,
        "Du lịch" to Icons.Default.Flight,
        "Hóa đơn & Thanh toán" to Icons.Default.Receipt,
        "Học hỏi" to Icons.Default.School,
        "Tâm linh" to Icons.Default.SelfImprovement,
        "Sức khỏe & Thể hình" to Icons.Default.FitnessCenter,
        "Tự chăm sóc" to Icons.Default.Spa
    )

    fun getIcon(categoryName: String?): ImageVector {
        return iconMap[categoryName] ?: Icons.Default.EditNote // Icon mặc định
    }
}


data class HabitCategory(
    val name: String,
    val icon: ImageVector
)

// Dùng provider để tạo danh sách, đảm bảo dữ liệu luôn đồng bộ
private val habitCategories = HabitIconProvider.categories.map { categoryName ->
    HabitCategory(categoryName, HabitIconProvider.getIcon(categoryName))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thói quen mới") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            CreateNewHabitButton(navController = navController)
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f), color = CardDarkBackground)
                Text(
                    text = "Hoặc",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Divider(modifier = Modifier.weight(1f), color = CardDarkBackground)
            }
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(habitCategories) { category ->
                    HabitCategoryItem(
                        category = category,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun CreateNewHabitButton(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .clickable {
                // SỬA LỖI Ở ĐÂY:
                // Điều hướng thẳng đến route gốc.
                // Các tham số không được truyền sẽ tự động là null.
                navController.navigate(Routes.CREATE_HABIT)
            }
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Tạo mới",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(28.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                .padding(4.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Tạo thói quen mới",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun HabitCategoryItem(category: HabitCategory, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val route = Routes.HABIT_DETAIL.replace("{categoryName}", category.name)
                navController.navigate(route)
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(30.dp),
                imageVector = category.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = category.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}