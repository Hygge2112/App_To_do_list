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
import androidx.compose.material.icons.automirrored.filled.MenuBook
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
import com.example.to_do_list.ui.theme.CardDarkBackground

// Data class để định nghĩa một thói quen gợi ý
data class SuggestedHabit(
    val name: String,
    val icon: ImageVector
)

// --- DỮ LIỆU GỢI Ý ---

private val focusSuggestions = listOf(
    SuggestedHabit("Học một kỹ năng mới", Icons.Default.School),
    SuggestedHabit("Hoàn thành nhiệm vụ dự án", Icons.Default.Task),
    SuggestedHabit("Ngủ 8 tiếng", Icons.Default.Bedtime),
    SuggestedHabit("Đặt 3 nhiệm vụ ưu tiên mỗi ngày", Icons.Default.PriorityHigh),
    SuggestedHabit("Thiền", Icons.Default.SelfImprovement),
    SuggestedHabit("Bài tập", Icons.Default.FitnessCenter),
    SuggestedHabit("Tham dự một sự kiện", Icons.Default.Event),
    SuggestedHabit("Kế hoạch kỳ nghỉ", Icons.Default.Flight)
)

private val workSuggestions = listOf(
    SuggestedHabit("Hoàn thành báo cáo dự án", Icons.Default.Description),
    SuggestedHabit("Tham dự cuộc họp nhóm", Icons.Default.Groups),
    SuggestedHabit("Cập nhật sơ yếu lý lịch", Icons.Default.ContactPage),
    SuggestedHabit("Gửi báo cáo chi phí", Icons.Default.ReceiptLong),
    SuggestedHabit("Sắp xếp tập tin", Icons.Default.Folder),
    SuggestedHabit("Chuẩn bị bài thuyết trình", Icons.Default.Slideshow),
    SuggestedHabit("Kiểm tra email", Icons.Default.Email),
    SuggestedHabit("Đánh giá hiệu suất của nhóm", Icons.Default.StarRate),
    SuggestedHabit("Nghiên cứu xu hướng ngành", Icons.Default.TrendingUp),
    SuggestedHabit("Hợp tác nhóm", Icons.Default.Handshake)

)

// --- THÊM DỮ LIỆU MỚI CHO MỤC "RIÊNG TƯ" ---
private val privateSuggestions = listOf(
    SuggestedHabit("Lên kế hoạch đi chơi cuối tuần", Icons.Default.Weekend),
    SuggestedHabit("Viết nhật ký", Icons.AutoMirrored.Filled.MenuBook),
    SuggestedHabit("Lên lịch một ngày spa", Icons.Default.Spa),
    SuggestedHabit("Cập nhật ngân sách cá nhân", Icons.Default.AccountBalanceWallet),
    SuggestedHabit("Gọi cho một người bạn", Icons.Default.Call),
    SuggestedHabit("Hãy thử một công thức mới", Icons.Default.Restaurant),
    SuggestedHabit("Dành thời gian cho một sở thích", Icons.Default.Favorite),
    SuggestedHabit("Đọc sách để giải trí", Icons.Default.Book),
    SuggestedHabit("Xem một bộ phim", Icons.Default.Movie),
    SuggestedHabit("Sắp xếp ảnh", Icons.Default.PhotoLibrary),
    SuggestedHabit("Tạo một bảng tầm nhìn", Icons.Default.Dashboard),
    SuggestedHabit("Lên kế hoạch cho một đêm chơi game", Icons.Default.VideogameAsset),
    SuggestedHabit("Tình nguyện", Icons.Default.VolunteerActivism),
    SuggestedHabit("Viết thư cho chính mình", Icons.Default.Mail),
    SuggestedHabit("Dọn dẹp một căn phòng", Icons.Default.CleaningServices)
)


private val shoppingSuggestions = listOf(
    SuggestedHabit("Tạo danh sách mua sắm", Icons.Default.ListAlt),
    SuggestedHabit("Lên kế hoạch mua sắm tạp hóa", Icons.Default.LocalGroceryStore),
    SuggestedHabit("Theo dõi hàng tồn kho", Icons.Default.Inventory),
    SuggestedHabit("Đặt ngân sách hàng tháng", Icons.Default.Savings),
    SuggestedHabit("So sánh giá cửa hàng", Icons.Default.CompareArrows),
    SuggestedHabit("Thêm vào danh sách yêu thích", Icons.Default.FavoriteBorder),
    SuggestedHabit("Kiểm tra phiếu giảm giá", Icons.Default.ConfirmationNumber),
    SuggestedHabit("Mua nhu yếu phẩm", Icons.Default.ShoppingBasket),
    SuggestedHabit("Theo dõi chi tiêu", Icons.Default.TrackChanges)
)


// --- THÊM DỮ LIỆU MỚI CHO MỤC "TÀI CHÍNH" ---
private val financeSuggestions = listOf(
    SuggestedHabit("Xem xét ngân sách hàng tháng", Icons.Default.AccountBalance),
    SuggestedHabit("Đặt mục tiêu tiết kiệm", Icons.Default.Savings),
    SuggestedHabit("Thanh toán hóa đơn đúng hạn", Icons.Default.ReceiptLong),
    SuggestedHabit("Kiểm tra số dư thẻ tín dụng", Icons.Default.CreditCard),
    SuggestedHabit("Theo dõi chi tiêu hàng ngày", Icons.Default.TrackChanges),
    SuggestedHabit("Tạo một kế hoạch tài chính", Icons.Default.RequestQuote),
    SuggestedHabit("Chuẩn bị cho mùa thuế", Icons.Default.Percent),
    SuggestedHabit("Xem lại chính sách bảo hiểm", Icons.Default.Policy),
    SuggestedHabit("Kế hoạch nghỉ hưu", Icons.Default.Chair),
    SuggestedHabit("Hủy đăng ký không sử dụng", Icons.Default.Cancel),
    SuggestedHabit("Đánh giá các lựa chọn cho vay", Icons.Default.CreditScore),
    SuggestedHabit("Lập quỹ khẩn cấp", Icons.Default.Emergency)
)

// --- THÊM DỮ LIỆU MỚI CHO MỤC "GIA ĐÌNH" ---
private val familySuggestions = listOf(
    SuggestedHabit("Lên lịch đêm trò chơi gia đình", Icons.Default.Casino),
    SuggestedHabit("Bài tập về nhà của trẻ em", Icons.Default.School),
    SuggestedHabit("Lên kế hoạch kỳ nghỉ", Icons.Default.FlightTakeoff),
    SuggestedHabit("Lễ kỷ niệm gia đình", Icons.Default.Celebration),
    SuggestedHabit("Sắp xếp ảnh gia đình", Icons.Default.PhotoAlbum),
    SuggestedHabit("Cùng nhau nấu một bữa ăn", Icons.Default.SoupKitchen),
    SuggestedHabit("Lên kế hoạch đi chơi", Icons.Default.Park),
    SuggestedHabit("Cùng nhau tình nguyện", Icons.Default.VolunteerActivism)
)

// --- THÊM DỮ LIỆU MỚI CHO MỤC "XÃ HỘI" ---
private val socialSuggestions = listOf(
    SuggestedHabit("Lên kế hoạch ăn tối với bạn bè", Icons.Default.DinnerDining),
    SuggestedHabit("Tổ chức một bữa tiệc ", Icons.Default.Celebration),
    SuggestedHabit("Tham dự một sự kiện kết nối", Icons.Default.ConnectWithoutContact),
    SuggestedHabit("Tham gia một câu lạc bộ", Icons.Default.Groups),
    SuggestedHabit("Bắt kịp với những người bạn cũ", Icons.Default.People),
    SuggestedHabit("Gửi thiệp sinh nhật", Icons.Default.CardGiftcard),
    SuggestedHabit("Tham gia các sự kiện cộng đồng", Icons.Default.Deck),
    SuggestedHabit("Tình nguyện cùng bạn bè", Icons.Default.VolunteerActivism),
    SuggestedHabit("Bắt đầu một câu lạc bộ sách", Icons.AutoMirrored.Filled.MenuBook),
    SuggestedHabit("Tổ chức trò chơi", Icons.Default.VideogameAsset)
)

// --- THÊM DỮ LIỆU MỚI CHO MỤC "CUỘC HẸN" ---
private val appointmentSuggestions = listOf(
    SuggestedHabit("Lên kế hoạch cho một chuyến đi khám bác sĩ", Icons.Default.MedicalServices),
    SuggestedHabit("Thiết lập một buổi trị liệu", Icons.Default.SelfImprovement),
    SuggestedHabit("Đặt dịch vụ mát-xa", Icons.Default.Spa),
    SuggestedHabit("Lập kế hoạch kiểm tra thú cưng", Icons.Default.Pets),
    SuggestedHabit("Lên lịch thăm khám nha khoa", Icons.Default.MedicalInformation),
    SuggestedHabit("Xem lại các cuộc hẹn sắp tới", Icons.Default.EventAvailable),
    SuggestedHabit("Lên lịch lại các cuộc hẹn bị nhỡ", Icons.Default.EditCalendar),
    SuggestedHabit("Lên lịch kiểm tra phòng thí nghiệm", Icons.Default.Science)
)


// --- THÊM DỮ LIỆU MỚI CHO MỤC "DU LỊCH" ---
private val travelSuggestions = listOf(
    SuggestedHabit("Lên kế hoạch cho chuyến đi sắp tới", Icons.Default.Explore),
    SuggestedHabit("Đặt mục tiêu du lịch", Icons.Default.Flag),
    SuggestedHabit("Đặt vé vận chuyển", Icons.Default.Commute),
    SuggestedHabit("Đặt chỗ ở", Icons.Default.Hotel),
    SuggestedHabit("Tạo một danh sách đóng gói", Icons.Default.Luggage),
    SuggestedHabit("Đặt ngân sách du lịch", Icons.Default.AttachMoney),
    SuggestedHabit("Tải xuống bản đồ ngoại tuyến", Icons.Default.Map),
    SuggestedHabit("Tạo một kế hoạch an toàn du lịch", Icons.Default.HealthAndSafety),
    SuggestedHabit("Kế hoạch cho nhu cầu sức khỏe", Icons.Default.MedicalServices),
    SuggestedHabit("Theo dõi thói quen du lịch", Icons.Default.Hiking),
    SuggestedHabit("Kiểm tra dự báo thời tiết", Icons.Default.WbSunny),
    SuggestedHabit("Theo dõi chi phí đi lại", Icons.Default.ReceiptLong),
    SuggestedHabit("Ghi lại kỷ niệm du lịch", Icons.Default.PhotoCamera),
    SuggestedHabit("Khám phá ẩm thực địa phương", Icons.Default.RestaurantMenu),
    SuggestedHabit("Lên kế hoạch thời gian thư giãn", Icons.Default.SelfImprovement)
)


// --- THÊM DỮ LIỆU MỚI CHO MỤC "HÓA ĐƠN & THANH TOÁN" ---
private val billsAndPaymentsSuggestions = listOf(
    SuggestedHabit("Thanh toán hóa đơn điện", Icons.Default.Lightbulb),
    SuggestedHabit("Thanh toán hóa đơn nước", Icons.Default.WaterDrop),
    SuggestedHabit("Thanh toán hóa đơn Internet", Icons.Default.Wifi),
    SuggestedHabit("Thanh toán tiền thuê nhà", Icons.Default.Home),
    SuggestedHabit("Thanh toán thẻ tín dụng", Icons.Default.CreditCard),
    SuggestedHabit("Lập lịch thanh toán tự động", Icons.Default.Schedule),
    SuggestedHabit("Kiểm tra lại các hóa đơn đã thanh toán", Icons.Default.History),
    SuggestedHabit("Lập ngân sách cho các hóa đơn", Icons.Default.AccountBalanceWallet)
)


// --- THÊM DỮ LIỆU MỚI CHO MỤC "HỌC HỎI" ---
private val learningSuggestions = listOf(
    SuggestedHabit("Các khóa học trực tuyến", Icons.Default.LaptopChromebook),
    SuggestedHabit("Đọc sách giáo dục", Icons.Default.Book),
    SuggestedHabit("Tham gia nhóm học tập", Icons.Default.Groups),
    SuggestedHabit("Xem phim tài liệu", Icons.Default.Movie),
    SuggestedHabit("Học ngôn ngữ mới", Icons.Default.Translate),
    SuggestedHabit("Tham dự hội thảo", Icons.Default.CoPresent),
    SuggestedHabit("Theo dõi podcast giáo dục", Icons.Default.Podcasts),
    SuggestedHabit("Khám phá các chủ đề mới", Icons.Default.Search),
    SuggestedHabit("Làm bài kiểm tra", Icons.Default.Quiz),
    SuggestedHabit("Thực hành viết mã", Icons.Default.Code),
    SuggestedHabit("Viết bài luận", Icons.Default.EditNote),
    SuggestedHabit("Đặt mục tiêu học tập", Icons.Default.Flag)
)


// --- THÊM DỮ LIỆU MỚI CHO MỤC "TÂM LINH" ---
private val spiritualSuggestions = listOf(
    SuggestedHabit("Thiền hàng ngày", Icons.Default.SelfImprovement),
    SuggestedHabit("Tham dự các buổi họp mặt tâm linh", Icons.Default.Groups),
    SuggestedHabit("Đọc văn bản tâm linh", Icons.Default.MenuBook),
    SuggestedHabit("Nhật ký suy ngẫm", Icons.Default.EditNote),
    SuggestedHabit("Thực hành chánh niệm", Icons.Default.Psychology),
    SuggestedHabit("Đi dạo trong thiên nhiên", Icons.Default.Forest),
    SuggestedHabit("Lời cầu nguyện tạ ơn", Icons.Default.VolunteerActivism),
    SuggestedHabit("Tình nguyện vì mục đích", Icons.Default.Handshake),
    SuggestedHabit("Tham gia vào các nghi lễ", Icons.Default.Celebration),
    SuggestedHabit("Khám phá những niềm tin khác", Icons.Default.TravelExplore),
    SuggestedHabit("Tạo một bảng tầm nhìn", Icons.Default.Dashboard),
    SuggestedHabit("Kết nối với cộng đồng", Icons.Default.ConnectWithoutContact),
    SuggestedHabit("Tham gia các lớp học yoga", Icons.Default.FitnessCenter),
    SuggestedHabit("Suy ngẫm về mục đích", Icons.Default.FilterCenterFocus),
    SuggestedHabit("Giải độc kỹ thuật số", Icons.Default.NoCell)
)


// --- THÊM DỮ LIỆU MỚI CHO MỤC "SỨC KHỎE & THỂ HÌNH" ---
private val healthAndFitnessSuggestions = listOf(
    SuggestedHabit("Đánh răng", Icons.Default.CleanHands),
    SuggestedHabit("Theo dõi lượng calo", Icons.Default.MonitorWeight),
    SuggestedHabit("Đặt mục tiêu số bước hàng ngày", Icons.Default.DirectionsWalk),
    SuggestedHabit("Hạn chế đường", Icons.Default.NoFood),
    SuggestedHabit("Tập yoga", Icons.Default.SelfImprovement),
    SuggestedHabit("Tránh uống rượu", Icons.Default.NoDrinks),
    SuggestedHabit("Hít thở sâu", Icons.Default.Air),
    SuggestedHabit("Thực hành lòng biết ơn", Icons.Default.Favorite),
    SuggestedHabit("Chuẩn bị kế hoạch bữa ăn lành mạnh", Icons.Default.RestaurantMenu),
    SuggestedHabit("Đi bộ 30 phút", Icons.Default.DirectionsWalk),
    SuggestedHabit("Uống 8 ly nước", Icons.Default.WaterDrop),
    SuggestedHabit("Bài tập", Icons.Default.FitnessCenter),
    SuggestedHabit("Thiền định", Icons.Default.SelfImprovement),
    SuggestedHabit("Ăn trái cây và rau quả", Icons.Default.Grass),
    SuggestedHabit("Ăn chay", Icons.Default.Eco)
)


// --- THÊM DỮ LIỆU MỚI CHO MỤC "TỰ CHĂM SÓC" ---
private val selfCareSuggestions = listOf(
    SuggestedHabit("Tắm thật lâu", Icons.Default.Bathtub),
    SuggestedHabit("Hít thở sâu", Icons.Default.Air),
    SuggestedHabit("Tập thể dục hàng ngày", Icons.Default.FitnessCenter),
    SuggestedHabit("Đi tắm", Icons.Default.Shower),
    SuggestedHabit("Không uống rượu", Icons.Default.NoDrinks),
    SuggestedHabit("Không có khói", Icons.Default.SmokeFree),
    SuggestedHabit("Thiền định", Icons.Default.SelfImprovement),
    SuggestedHabit("Tập thể dục thường xuyên", Icons.Default.FitnessCenter),
    SuggestedHabit("Triệt lông", Icons.Default.ContentCut),
    SuggestedHabit("Dành thời gian trong thiên nhiên", Icons.Default.Forest),
    SuggestedHabit("Đọc một cuốn sách tạo động lực", Icons.Default.Book),
    SuggestedHabit("Giới hạn thời gian sử dụng màn hình", Icons.Default.ScreenLockPortrait),
    SuggestedHabit("Theo dõi những thay đổi của cơ thể", Icons.Default.TrackChanges)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(navController: NavController, categoryName: String) {
    // --- CẬP NHẬT LOGIC ĐỂ CHỌN ĐÚNG DANH SÁCH ---
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
        "Tự chăm sóc" -> selfCareSuggestions // Thêm trường hợp mới
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
                // Truyền NavController vào item
                SuggestedHabitItem(
                    suggestion = suggestion,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun SuggestedHabitItem(suggestion: SuggestedHabit, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF2A2A2A))
            .clickable {
                // --- THAY ĐỔI Ở ĐÂY: Điều hướng đến CreateHabitScreen với tham số ---
                val route = "create_habit?habitName=${suggestion.name}"
                navController.navigate(route)
            }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = suggestion.icon,
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

