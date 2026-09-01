package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BiaarDarkCard
import com.example.ui.theme.BiaarDarkCardBorder
import com.example.ui.theme.BiaarDarkSurface
import com.example.ui.theme.BiaarEmerald
import com.example.ui.theme.BiaarGold
import com.example.ui.theme.BiaarGoldLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeDemoSheet(
    onDismiss: () -> Unit,
    onRecharge: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val packages = listOf(
        Triple(1000L, "باقة المبتدئ", "مجاني"),
        Triple(5000L, "باقة النجوم", "شائع"),
        Triple(15000L, "باقة المحترفين", "أفضل قيمة"),
        Triple(50000L, "باقة VIP الأسطورية", "نخبة")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BiaarDarkSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "شحن رصيد تجريبي مجاني",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            DemoNoticeBanner(
                customText = "💰 هذه العملات تجريبية ومجانية بالكامل 100% لمحاكاة ألعاب المنصة"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Packages list
            packages.forEach { (amount, titleAr, badgeAr) ->
                val formatted = NumberFormat.getNumberInstance(Locale.US).format(amount)
                val isVipPackage = amount >= 15000L

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isVipPackage) {
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF282012), Color(0xFF161F32))
                                )
                            } else {
                                Brush.horizontalGradient(
                                    listOf(BiaarDarkCard, Color(0xFF1A2338))
                                )
                            }
                        )
                        .border(
                            1.dp,
                            if (isVipPackage) BiaarGold.copy(alpha = 0.6f) else BiaarDarkCardBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onRecharge(amount) }
                        .padding(14.dp)
                        .testTag("recharge_package_$amount")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isVipPackage) BiaarGold.copy(alpha = 0.2f) else BiaarEmerald.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isVipPackage) Icons.Default.WorkspacePremium else Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = if (isVipPackage) BiaarGoldLight else BiaarEmerald,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "+$formatted عملة",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isVipPackage) BiaarGold else Color(0xFF2D3748)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = badgeAr,
                                            color = if (isVipPackage) Color.Black else TextPrimary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = titleAr,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Instant claim button
                        Button(
                            onClick = { onRecharge(amount) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isVipPackage) BiaarGold else BiaarEmerald
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "شحن مجاني",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
