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

data class SuggestedHabit(
    val name: String,
    val iconName: String
)

// CÁC DANH SÁCH GỢI Ý ĐÃ ĐƯỢC CHUẨN HÓA
private val focusSuggestions = listOf(
    SuggestedHabit("Học một kỹ năng mới", "School"),
    SuggestedHabit("Hoàn thành nhiệm vụ dự án", "Task"),
    SuggestedHabit("Ngủ 8 tiếng", "Bedtime"),
    SuggestedHabit("Đặt 3 nhiệm vụ ưu tiên mỗi ngày", "PriorityHigh"),
    SuggestedHabit("Thiền", "SelfImprovement"),
    SuggestedHabit("Tập thể dục", "FitnessCenter"),
    SuggestedHabit("Tham dự một sự kiện", "Event"),
    SuggestedHabit("Lên kế hoạch kỳ nghỉ", "Flight")
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

private val privateSuggestions = listOf(
    SuggestedHabit("Lên kế hoạch đi chơi cuối tuần", "Weekend"),
    SuggestedHabit("Viết nhật ký", "MenuBook"),
    SuggestedHabit("Lên lịch một ngày spa", "Spa"),
    SuggestedHabit("Cập nhật ngân sách cá nhân", "AccountBalanceWallet"),
    SuggestedHabit("Gọi cho một người bạn", "Call"),
    SuggestedHabit("Thử một công thức mới", "Restaurant"),
    SuggestedHabit("Dành thời gian cho một sở thích", "Favorite"),
    SuggestedHabit("Đọc sách để giải trí", "Book"),
    SuggestedHabit("Xem một bộ phim", "Movie"),
    SuggestedHabit("Sắp xếp ảnh", "PhotoLibrary"),
    SuggestedHabit("Tạo một bảng tầm nhìn", "Dashboard"),
    SuggestedHabit("Lên kế hoạch cho một đêm chơi game", "VideogameAsset"),
    SuggestedHabit("Tình nguyện", "VolunteerActivism"),
    SuggestedHabit("Viết thư", "Mail"),
    SuggestedHabit("Dọn dẹp một căn phòng", "CleaningServices")
)

private val shoppingSuggestions = listOf(
    SuggestedHabit("Tạo danh sách mua sắm", "ListAlt"),
    SuggestedHabit("Mua sắm tạp hóa", "LocalGroceryStore"),
    SuggestedHabit("Kiểm kê hàng tồn kho", "Inventory"),
    SuggestedHabit("Đặt ngân sách mua sắm", "Savings"),
    SuggestedHabit("So sánh giá", "CompareArrows"),
    SuggestedHabit("Thêm vào danh sách yêu thích", "FavoriteBorder"),
    SuggestedHabit("Kiểm tra phiếu giảm giá", "ConfirmationNumber"),
    SuggestedHabit("Mua nhu yếu phẩm", "ShoppingBasket"),
    SuggestedHabit("Theo dõi chi tiêu", "TrackChanges")
)

private val financeSuggestions = listOf(
    SuggestedHabit("Xem xét ngân sách hàng tháng", "AccountBalance"),
    SuggestedHabit("Đặt mục tiêu tiết kiệm", "Savings"),
    SuggestedHabit("Thanh toán hóa đơn đúng hạn", "ReceiptLong"),
    SuggestedHabit("Kiểm tra số dư thẻ tín dụng", "CreditCard"),
    SuggestedHabit("Theo dõi chi tiêu hàng ngày", "TrackChanges"),
    SuggestedHabit("Tạo một kế hoạch tài chính", "RequestQuote"),
    SuggestedHabit("Chuẩn bị cho mùa thuế", "Percent"),
    SuggestedHabit("Xem lại chính sách bảo hiểm", "Policy"),
    SuggestedHabit("Lên kế hoạch nghỉ hưu", "Chair"),
    SuggestedHabit("Hủy đăng ký không sử dụng", "Cancel"),
    SuggestedHabit("Đánh giá các lựa chọn cho vay", "CreditScore"),
    SuggestedHabit("Lập quỹ khẩn cấp", "Emergency")
)

private val familySuggestions = listOf(
    SuggestedHabit("Lên lịch đêm trò chơi gia đình", "Casino"),
    SuggestedHabit("Làm bài tập về nhà cùng con", "School"),
    SuggestedHabit("Lên kế hoạch kỳ nghỉ", "FlightTakeoff"),
    SuggestedHabit("Tổ chức lễ kỷ niệm", "Celebration"),
    SuggestedHabit("Sắp xếp album ảnh gia đình", "PhotoAlbum"),
    SuggestedHabit("Cùng nhau nấu một bữa ăn", "SoupKitchen"),
    SuggestedHabit("Lên kế hoạch đi chơi công viên", "Park"),
    SuggestedHabit("Cùng nhau tình nguyện", "VolunteerActivism")
)

private val socialSuggestions = listOf(
    SuggestedHabit("Lên kế hoạch ăn tối với bạn bè", "DinnerDining"),
    SuggestedHabit("Tổ chức một bữa tiệc", "Celebration"),
    SuggestedHabit("Tham dự sự kiện kết nối", "ConnectWithoutContact"),
    SuggestedHabit("Tham gia một câu lạc bộ", "Groups"),
    SuggestedHabit("Hẹn hò với những người bạn cũ", "People"),
    SuggestedHabit("Gửi thiệp sinh nhật", "CardGiftcard"),
    SuggestedHabit("Tham gia các sự kiện cộng đồng", "Deck"),
    SuggestedHabit("Tình nguyện cùng bạn bè", "VolunteerActivism"),
    SuggestedHabit("Bắt đầu một câu lạc bộ sách", "MenuBook"),
    SuggestedHabit("Tổ chức đêm chơi game", "VideogameAsset")
)

private val appointmentSuggestions = listOf(
    SuggestedHabit("Lên lịch khám bác sĩ", "MedicalServices"),
    SuggestedHabit("Đặt lịch buổi trị liệu", "SelfImprovement"),
    SuggestedHabit("Đặt dịch vụ mát-xa", "Spa"),
    SuggestedHabit("Lập kế hoạch kiểm tra thú cưng", "Pets"),
    SuggestedHabit("Lên lịch khám nha khoa", "MedicalInformation"),
    SuggestedHabit("Xem lại các cuộc hẹn sắp tới", "EventAvailable"),
    SuggestedHabit("Sắp xếp lại các cuộc hẹn", "EditCalendar"),
    SuggestedHabit("Lên lịch xét nghiệm", "Science")
)

private val travelSuggestions = listOf(
    SuggestedHabit("Nghiên cứu điểm đến", "Explore"),
    SuggestedHabit("Đặt mục tiêu du lịch", "Flag"),
    SuggestedHabit("Đặt vé máy bay/tàu xe", "Commute"),
    SuggestedHabit("Đặt chỗ ở", "Hotel"),
    SuggestedHabit("Lập danh sách đóng gói", "Luggage"),
    SuggestedHabit("Đặt ngân sách du lịch", "AttachMoney"),
    SuggestedHabit("Tải xuống bản đồ ngoại tuyến", "Map"),
    SuggestedHabit("Lập kế hoạch an toàn", "HealthAndSafety"),
    SuggestedHabit("Chuẩn bị thuốc men", "MedicalServices"),
    SuggestedHabit("Theo dõi hoạt động du lịch", "Hiking"),
    SuggestedHabit("Kiểm tra dự báo thời tiết", "WbSunny"),
    SuggestedHabit("Theo dõi chi phí chuyến đi", "ReceiptLong"),
    SuggestedHabit("Ghi lại kỷ niệm", "PhotoCamera"),
    SuggestedHabit("Khám phá ẩm thực địa phương", "RestaurantMenu"),
    SuggestedHabit("Lên kế hoạch thời gian thư giãn", "SelfImprovement")
)

private val billsAndPaymentsSuggestions = listOf(
    SuggestedHabit("Thanh toán hóa đơn điện", "Lightbulb"),
    SuggestedHabit("Thanh toán hóa đơn nước", "WaterDrop"),
    SuggestedHabit("Thanh toán hóa đơn Internet", "Wifi"),
    SuggestedHabit("Thanh toán tiền thuê nhà", "Home"),
    SuggestedHabit("Thanh toán thẻ tín dụng", "CreditCard"),
    SuggestedHabit("Lập lịch thanh toán tự động", "Schedule"),
    SuggestedHabit("Kiểm tra lại các hóa đơn", "History"),
    SuggestedHabit("Lập ngân sách cho các hóa đơn", "AccountBalanceWallet")
)

private val learningSuggestions = listOf(
    SuggestedHabit("Học khóa học trực tuyến", "LaptopChromebook"),
    SuggestedHabit("Đọc sách chuyên ngành", "Book"),
    SuggestedHabit("Tham gia nhóm học tập", "Groups"),
    SuggestedHabit("Xem phim tài liệu", "Movie"),
    SuggestedHabit("Học ngôn ngữ mới", "Translate"),
    SuggestedHabit("Tham dự hội thảo", "CoPresent"),
    SuggestedHabit("Nghe podcast giáo dục", "Podcasts"),
    SuggestedHabit("Nghiên cứu chủ đề mới", "Search"),
    SuggestedHabit("Làm bài kiểm tra", "Quiz"),
    SuggestedHabit("Thực hành viết mã", "Code"),
    SuggestedHabit("Viết bài luận", "EditNote"),
    SuggestedHabit("Đặt mục tiêu học tập", "Flag")
)

private val spiritualSuggestions = listOf(
    SuggestedHabit("Thiền hàng ngày", "SelfImprovement"),
    SuggestedHabit("Tham dự các buổi họp mặt tâm linh", "Groups"),
    SuggestedHabit("Đọc văn bản tâm linh", "MenuBook"),
    SuggestedHabit("Viết nhật ký suy ngẫm", "EditNote"),
    SuggestedHabit("Thực hành chánh niệm", "Psychology"),
    SuggestedHabit("Đi dạo trong thiên nhiên", "Forest"),
    SuggestedHabit("Thực hành lòng biết ơn", "VolunteerActivism"),
    SuggestedHabit("Tình nguyện vì một mục đích", "Handshake"),
    SuggestedHabit("Thực hành các nghi lễ", "Celebration"),
    SuggestedHabit("Khám phá những niềm tin khác", "TravelExplore"),
    SuggestedHabit("Tạo bảng tầm nhìn", "Dashboard"),
    SuggestedHabit("Kết nối với cộng đồng", "ConnectWithoutContact"),
    SuggestedHabit("Tham gia lớp học yoga", "FitnessCenter"),
    SuggestedHabit("Suy ngẫm về mục đích sống", "FilterCenterFocus"),
    SuggestedHabit("Giải độc kỹ thuật số", "NoCell")
)

private val healthAndFitnessSuggestions = listOf(
    SuggestedHabit("Đánh răng", "CleanHands"),
    SuggestedHabit("Theo dõi cân nặng", "MonitorWeight"),
    SuggestedHabit("Đi bộ đủ số bước", "DirectionsWalk"),
    SuggestedHabit("Hạn chế đường", "NoFood"),
    SuggestedHabit("Tập yoga", "SelfImprovement"),
    SuggestedHabit("Tránh uống rượu", "NoDrinks"),
    SuggestedHabit("Hít thở sâu", "Air"),
    SuggestedHabit("Thực hành lòng biết ơn", "Favorite"),
    SuggestedHabit("Chuẩn bị bữa ăn lành mạnh", "RestaurantMenu"),
    SuggestedHabit("Đi bộ 30 phút", "DirectionsWalk"),
    SuggestedHabit("Uống 8 ly nước", "WaterDrop"),
    SuggestedHabit("Tập thể dục", "FitnessCenter"),
    SuggestedHabit("Ăn nhiều trái cây và rau quả", "Grass"),
    SuggestedHabit("Ăn chay", "Eco")
)

private val selfCareSuggestions = listOf(
    SuggestedHabit("Tắm bồn", "Bathtub"),
    SuggestedHabit("Hít thở sâu", "Air"),
    SuggestedHabit("Tập thể dục hàng ngày", "FitnessCenter"),
    SuggestedHabit("Tắm vòi sen", "Shower"),
    SuggestedHabit("Không uống rượu", "NoDrinks"),
    SuggestedHabit("Bỏ thuốc lá", "SmokeFree"),
    SuggestedHabit("Thiền định", "SelfImprovement"),
    SuggestedHabit("Cắt tóc/chăm sóc móng", "ContentCut"),
    SuggestedHabit("Dành thời gian trong thiên nhiên", "Forest"),
    SuggestedHabit("Đọc sách tạo động lực", "Book"),
    SuggestedHabit("Giới hạn thời gian sử dụng màn hình", "ScreenLockPortrait"),
    SuggestedHabit("Theo dõi những thay đổi của cơ thể", "TrackChanges")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(navController: NavController, categoryName: String) {
    val suggestions = when (categoryName) {
        "Tập trung" -> focusSuggestions
        "Công việc" -> workSuggestions
        "Riêng tư" -> privateSuggestions
        "Mua sắm" -> shoppingSuggestions
        "Tài chính" -> financeSuggestions
        "Gia đình" -> familySuggestions
        "Xã hội" -> socialSuggestions
        "Cuộc hẹn" -> appointmentSuggestions
        "Du lịch" -> travelSuggestions
        "Hóa đơn & Thanh toán" -> billsAndPaymentsSuggestions
        "Học hỏi" -> learningSuggestions
        "Tâm linh" -> spiritualSuggestions
        "Sức khỏe & Thể hình" -> healthAndFitnessSuggestions
        "Tự chăm sóc" -> selfCareSuggestions
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
    // Lấy ImageVector từ provider để hiển thị
    val iconVector = HabitIconProvider.getIcon(suggestion.iconName, null)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF2A2A2A))
            .clickable {
                // Truyền cả 3 tham số khi điều hướng
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