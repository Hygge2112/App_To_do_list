package com.example.to_do_list.introduce

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_do_list.ui.theme.*

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val iconColor: Color
)

private val pages = listOf(
    OnboardingPage(
        icon = Icons.Default.Psychology,
        title = "Giải phóng tâm trí của bạn",
        description = "Ghi lại mọi thứ cần làm để giảm căng thẳng và tập trung hơn vào từng khoảnh khắc.",
        iconColor = AccentBlue
    ),
    OnboardingPage(
        icon = Icons.Default.TrackChanges,
        title = "Tập trung vào điều quan trọng",
        description = "Dễ dàng sắp xếp ưu tiên và theo dõi tiến độ, đảm bảo bạn không bao giờ bỏ lỡ deadline.",
        iconColor = AccentOrange
    ),
    OnboardingPage(
        icon = Icons.Outlined.CheckCircleOutline,
        title = "Chinh phục mục tiêu lớn",
        description = "Chia nhỏ những dự án phức tạp thành các bước đơn giản, dễ quản lý và thực hiện.",
        iconColor = AccentRed
    ),
    OnboardingPage(
        icon = Icons.Default.Security,
        title = "An toàn và Riêng tư",
        description = "Dữ liệu của bạn được bảo vệ. Chúng tôi tôn trọng quyền riêng tư của bạn trên hết.",
        iconColor = AccentPurple
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit,
    onLoginClick: () -> Unit // Thêm tham số mới
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { pageIndex ->
                    OnboardingPageContent(page = pages[pageIndex])
                }

                Row(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pages.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else TextSecondaryDark
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .size(10.dp)
                                .coloredShadow(color, shadowRadius = 8.dp, alpha = 0.5f)
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onGetStartedClick, // Sử dụng callback
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Bắt đầu miễn phí", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = onLoginClick) { // Sử dụng callback
                        Text(
                            "Tôi đã có tài khoản",
                            color = TextSecondaryDark,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
            ) {
                Text(
                    text = "Hygge To-do list",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Icon(
            imageVector = page.icon,
            contentDescription = page.title,
            modifier = Modifier.size(150.dp),
            tint = page.iconColor
        )
        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

fun Modifier.coloredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 20.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp
) = this.drawBehind {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparent = color.copy(alpha= 0f).toArgb()

    this.drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparent
        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OnboardingScreenPreview() {
    To_do_listTheme {
        OnboardingScreen(onGetStartedClick = {}, onLoginClick = {})
    }
}
