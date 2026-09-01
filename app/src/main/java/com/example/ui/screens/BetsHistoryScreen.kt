package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetRecord
import com.example.data.model.BetStatus
import com.example.ui.components.DemoNoticeBanner
import com.example.ui.theme.BiaarCyan
import com.example.ui.theme.BiaarDarkCard
import com.example.ui.theme.BiaarDarkCardBorder
import com.example.ui.theme.BiaarEmerald
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
fun BetsHistoryScreen(
    viewModel: BiaarViewModel,
    modifier: Modifier = Modifier
) {
    val bets by viewModel.betsHistory.collectAsState()
    val activeFilter by viewModel.showBetHistoryFilter.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    val filteredBets = when (activeFilter) {
        "WON" -> bets.filter { it.status == BetStatus.WON }
        "LOST" -> bets.filter { it.status == BetStatus.LOST }
        "ACTIVE" -> bets.filter { it.status == BetStatus.ACTIVE }
        else -> bets
    }

    val totalBetsCount = bets.size
    val totalWonCount = bets.count { it.status == BetStatus.WON }
    val winRate = if (totalBetsCount > 0) (totalWonCount.toFloat() / totalBetsCount * 100).toInt() else 0

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "سجل المراهنات والجولات",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "متابعة نتائج جميع الجولات والرهانات التجريبية المحققة",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                DemoNoticeBanner(
                    customText = "📊 جميع الأرباح والخسائر المسجلة هي محاكاة برصيد بيار التجريبي فقط"
                )
            }
        }

        // Summary Stats Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, BiaarDarkCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BiaarDarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalBetsCount",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "إجمالي الجولات",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .width(1.dp)
                            .background(BiaarDarkCardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalWonCount",
                            color = BiaarEmerald,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "الجولات الرابحة",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .width(1.dp)
                            .background(BiaarDarkCardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$winRate%",
                            color = BiaarGoldLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "نسبة الفوز",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Filter Tabs
        item {
            val filterOptions = listOf(
                Pair("ALL", "الكل (${bets.size})"),
                Pair("WON", "الرابحة (${bets.count { it.status == BetStatus.WON }})"),
                Pair("LOST", "الخاسرة (${bets.count { it.status == BetStatus.LOST }})"),
                Pair("ACTIVE", "قيد اللعب (${bets.count { it.status == BetStatus.ACTIVE }})")
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { (key, label) ->
                    val isSelected = activeFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) BiaarEmerald else BiaarDarkCard)
                            .border(
                                1.dp,
                                if (isSelected) BiaarEmerald else BiaarDarkCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setBetHistoryFilter(key) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("bet_filter_$key")
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Bets List
        if (filteredBets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "لا توجد رهانات مطابقة في هذا القسم",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(filteredBets) { bet ->
                BetRecordItem(bet = bet)
            }
        }
    }
}

@Composable
private fun BetRecordItem(bet: BetRecord) {
    val formattedStake = NumberFormat.getNumberInstance(Locale.US).format(bet.stakeAmount)
    val formattedPayout = NumberFormat.getNumberInstance(Locale.US).format(bet.payoutAmount)

    val (statusText, statusBg, statusTextColor) = when (bet.status) {
        BetStatus.WON -> Triple("ربح +$formattedPayout", BiaarEmerald.copy(alpha = 0.2f), BiaarEmerald)
        BetStatus.LOST -> Triple("خسارة -$formattedStake", BiaarRuby.copy(alpha = 0.2f), BiaarRuby)
        BetStatus.ACTIVE -> Triple("قيد اللعب", BiaarGold.copy(alpha = 0.2f), BiaarGoldLight)
        BetStatus.CANCELLED -> Triple("مسترد", Color.Gray.copy(alpha = 0.2f), Color.LightGray)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(1.dp, BiaarDarkCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = BiaarDarkCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF202C42)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (bet.status) {
                                BetStatus.WON -> Icons.Default.CheckCircle
                                BetStatus.LOST -> Icons.Default.RemoveCircle
                                else -> Icons.Default.HourglassBottom
                            },
                            contentDescription = null,
                            tint = statusTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = bet.gameTitleAr,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "غرفة: ${bet.roomId} • المنافس: ${bet.opponentNameAr}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Match Detail Note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1726))
                    .padding(8.dp)
            ) {
                Text(
                    text = bet.matchDetailsAr,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom details: Stake, Multiplier, Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "الرهان: $formattedStake عملة",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "المضاعف: ${bet.multiplier}x",
                        color = BiaarCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = bet.timeAgoAr,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
