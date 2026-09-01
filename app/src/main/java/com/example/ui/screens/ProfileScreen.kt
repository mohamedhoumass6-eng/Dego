package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.User
import com.example.data.model.VipLevel
import com.example.ui.components.DemoNoticeBanner
import com.example.ui.theme.BiaarCyan
import com.example.ui.theme.BiaarDarkCard
import com.example.ui.theme.BiaarDarkCardBorder
import com.example.ui.theme.BiaarDarkSurface
import com.example.ui.theme.BiaarEmerald
import com.example.ui.theme.BiaarEmeraldLight
import com.example.ui.theme.BiaarGold
import com.example.ui.theme.BiaarGoldLight
import com.example.ui.theme.BiaarRuby
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BiaarViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: BiaarViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    var soundEnabled by remember { mutableStateOf(user.isSoundEnabled) }
    var hapticEnabled by remember { mutableStateOf(user.isHapticEnabled) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "الملف الشخصي والإعدادات",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "بيانات اللاعب، المستوى، الأوسمة، وإعدادات التجربة",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                DemoNoticeBanner(
                    customText = "🎮 حساب تجريبي متكامل مع مستويات VIP وسجل إحصائي واقعي"
                )
            }
        }

        // Profile Identity Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, BiaarDarkCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = BiaarDarkCard),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1B283E), Color(0xFF111827))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, Color(user.vipLevel.colorHex), CircleShape)
                                    .background(Color(0xFF1E293B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.biaar_logo),
                                    contentDescription = "Avatar",
                                    modifier = Modifier.size(54.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(user.vipLevel.colorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.fullNameAr,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Text(
                                text = "@${user.username} • ${user.rankAr}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(user.vipLevel.colorHex).copy(alpha = 0.2f))
                                    .border(1.dp, Color(user.vipLevel.colorHex), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "رتبة VIP: ${user.vipLevel.titleAr} (${user.vipLevel.multiplierBonus})",
                                    color = Color(user.vipLevel.colorHex),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress to Next VIP
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "التقدم نحو المستوى الأسطوري",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "78%",
                                color = BiaarEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.78f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = BiaarEmerald,
                            trackColor = Color(0xFF24324D)
                        )
                    }
                }
            }
        }

        // Performance Statistics Grid
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                titleAr = "سجل الأداء والإحصائيات",
                subtitleAr = "أداء الجولات والانتصارات التراكمية"
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBox(
                    title = "الجولات",
                    value = "${user.gamesPlayed}",
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    title = "الفوز",
                    value = "${user.gamesWon}",
                    color = BiaarEmerald,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    title = "الخسارة",
                    value = "${user.gamesLost}",
                    color = BiaarRuby,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    title = "أطول سلسلة",
                    value = "${user.bestStreak} 🔥",
                    color = BiaarGoldLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Switch Demo Profile Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                titleAr = "تبديل الحساب التجريبي",
                subtitleAr = "جرّب المنصة بحسابات ذات رتب ومحافظ مختلفة"
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val demoProfiles = listOf(
                    Pair(0, "فهد السبيعي (VIP ماسي)"),
                    Pair(1, "سلطان النرد (VIP أسطوري)"),
                    Pair(2, "أميرة البلوت (VIP ذهبي)")
                )

                demoProfiles.forEach { (index, title) ->
                    OutlinedButton(
                        onClick = { viewModel.switchDemoAccount(index) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("switch_account_btn_$index"),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BiaarEmerald)
                    ) {
                        Text(
                            text = title.split(" ").first(),
                            color = BiaarEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Achievements List
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                titleAr = "الأوسمة والإنجازات",
                subtitleAr = "شارات البطولات والألعاب المحققة"
            )
            Spacer(modifier = Modifier.height(6.dp))

            val achievements = listOf(
                Triple("👑 ملك البلوت", "فوز بـ 50 جولة بلوت صن ملكية", true),
                Triple("🎲 قاهر النرد", "تحقيق 5 رميات نرد متتالية رابحة", true),
                Triple("🏆 مليونير بيار التجريبي", "تخطي حاجز 50,000 عملة تجريبية", true),
                Triple("🎯 أستاذ الكيرم", "إسقاط الملكة وتغطيتها 20 مرة", false)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                achievements.forEach { (title, desc, unlocked) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (unlocked) BiaarGold.copy(alpha = 0.4f) else BiaarDarkCardBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = BiaarDarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (unlocked) Icons.Default.MilitaryTech else Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (unlocked) BiaarGold else TextMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = title,
                                        color = if (unlocked) TextPrimary else TextMuted,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = desc,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (unlocked) BiaarEmerald.copy(alpha = 0.2f) else Color(0xFF24324D))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (unlocked) "مكتمل" else "قيد الإنجاز",
                                    color = if (unlocked) BiaarEmerald else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Settings & Switches
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                titleAr = "إعدادات التطبيق",
                subtitleAr = "التحكم في المؤثرات واللغة والإشعارات"
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, BiaarDarkCardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = BiaarDarkCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    SettingToggleRow(
                        icon = Icons.Default.VolumeUp,
                        titleAr = "المؤثرات الصوتية للألعاب",
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            viewModel.updateSettings(soundEnabled, hapticEnabled)
                        }
                    )
                    SettingToggleRow(
                        icon = Icons.Default.Vibration,
                        titleAr = "الاهتزاز والتفاعل اللمسي (Haptic)",
                        checked = hapticEnabled,
                        onCheckedChange = {
                            hapticEnabled = it
                            viewModel.updateSettings(soundEnabled, hapticEnabled)
                        }
                    )
                    SettingToggleRow(
                        icon = Icons.Default.Notifications,
                        titleAr = "إشعارات الغرف والبطولات",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            }
        }

        // Sign Out Button
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp)
                    .testTag("profile_logout_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B1B1F)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BiaarRuby.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = BiaarRuby,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تسجيل الخروج من الحساب",
                    color = BiaarRuby,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BiaarDarkCard)
            .border(1.dp, BiaarDarkCardBorder, RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titleAr: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BiaarEmerald,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = titleAr,
                color = TextPrimary,
                fontSize = 13.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = BiaarEmerald,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = Color(0xFF1E293B)
            )
        )
    }
}
