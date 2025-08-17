package com.example.to_do_list.ui.theme.main.settings

import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.to_do_list.R
import com.example.to_do_list.auth.AuthViewModel
import com.example.to_do_list.navigation.Routes
import java.util.Locale
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.graphics.Color

@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    // Lấy locale hiện tại để set trạng thái selected
    val currentTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    val initialLang = if (currentTags.isEmpty()) Locale.getDefault().language else currentTags
    var selectedLang by remember { mutableStateOf(if (initialLang.startsWith("vi")) "vi" else "en") }

    // OK dùng stringResource trong composable
    val labelVi = stringResource(R.string.lang_vi)
    val labelEn = stringResource(R.string.lang_en)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.language_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = selectedLang == "vi",
                    onClick = {
                        if (selectedLang != "vi") {
                            selectedLang = "vi"
                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("vi"))
                            // dùng context.getString trong onClick (không dùng stringResource ở đây)
                            Toast.makeText(
                                context,
                                context.getString(R.string.switched_to_vi),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    label = { Text(labelVi) }
                )

                FilterChip(
                    selected = selectedLang == "en",
                    onClick = {
                        if (selectedLang != "en") {
                            selectedLang = "en"
                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
                            Toast.makeText(
                                context,
                                context.getString(R.string.switched_to_en),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    label = { Text(labelEn) }
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    authViewModel.logoutUser()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.logout), fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
