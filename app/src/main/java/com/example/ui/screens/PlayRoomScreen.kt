package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DemoNoticeBanner
import com.example.ui.theme.BiaarCyan
import com.example.ui.theme.BiaarCyanLight
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
import com.example.ui.viewmodel.BiaarNavTab
import com.example.ui.viewmodel.BiaarViewModel
import com.example.ui.viewmodel.DicePrediction
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PlayRoomScreen(
    viewModel: BiaarViewModel,
    modifier: Modifier = Modifier
) {
    val diceState by viewModel.diceGameState.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val stakes = listOf(50L, 100L, 250L, 500L, 1000L, 2500L)

    val rollRotation by animateFloatAsState(
        targetValue = if (diceState.isRolling) 360f else 0f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "dice_rotation"
    )

    val formattedBalance = NumberFormat.getNumberInstance(Locale.US).format(user.demoBalance)
    val possibleWin = (diceState.selectedStake * diceState.selectedPrediction.multiplier).toLong()
    val formattedPossibleWin = NumberFormat.getNumberInstance(Locale.US).format(possibleWin)
    val sum = diceState.dice1 + diceState.dice2

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Header
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ساحة نرد LGDAH الذهبي",
                                color = TextPrimary,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🎲",
                                fontSize = 18.sp
                            )
                        }
                        Text(
                            text = "اختر التوقع وقيمة الرهان ثم ارمِ النرد لتحقيق الفوز",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // Demo Balance Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B263B))
                            .border(1.dp, BiaarGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectTab(BiaarNavTab.WALLET) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = BiaarGoldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formattedBalance,
                                color = BiaarGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                DemoNoticeBanner(
                    customText = "🎲 جميع الرهانات تتم برصيد تجريبي مجاني بالكامل"
                )
            }
        }

        // 2. Dice Interactive Board Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .border(1.5.dp, BiaarGold.copy(alpha = 0.5f), RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF1E2D4A), Color(0xFF0F1726))
                            )
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Match Players Header (You vs Bot)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User side
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(BiaarEmerald.copy(alpha = 0.2f))
                                    .border(1.5.dp, BiaarEmerald, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "أنت",
                                    color = BiaarEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = user.fullNameAr,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "فوز: ${diceState.playerRoundScore}",
                                    color = BiaarEmeraldLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Round counter badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF24324D))
                                .border(1.dp, BiaarGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "الجولة ${diceState.roundCount}",
                                color = BiaarGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Bot side
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = diceState.botNameAr,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "فوز: ${diceState.botRoundScore}",
                                    color = BiaarRuby,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(BiaarRuby.copy(alpha = 0.2f))
                                    .border(1.5.dp, BiaarRuby, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = null,
                                    tint = BiaarRuby,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // The 2 Animated Dice
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(22.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DiceFaceView(
                            value = diceState.dice1,
                            rotation = rollRotation,
                            isRolling = diceState.isRolling
                        )
                        DiceFaceView(
                            value = diceState.dice2,
                            rotation = -rollRotation,
                            isRolling = diceState.isRolling
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sum & Result Highlight
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .border(1.dp, BiaarGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "مجموع النرد: ",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "$sum (${diceState.dice1} + ${diceState.dice2})",
                                color = BiaarGoldLight,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = diceState.statusMessageAr,
                        color = when (diceState.lastResultWin) {
                            true -> BiaarEmerald
                            false -> BiaarRuby
                            null -> TextSecondary
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 3. Predictions Selector
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "اختر توقعك للرمية القادمة:",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DicePrediction.values().forEach { prediction ->
                        val isSelected = diceState.selectedPrediction == prediction
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF2B200A), Color(0xFF16233B))
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            listOf(BiaarDarkCard, BiaarDarkCard)
                                        )
                                    }
                                )
                                .border(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) BiaarGold else BiaarDarkCardBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.setDicePrediction(prediction) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("dice_prediction_${prediction.name}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = prediction.titleAr,
                                        color = if (isSelected) BiaarGoldLight else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = prediction.descAr,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) BiaarGold else Color(0xFF24324D))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "${prediction.multiplier}x",
                                        color = if (isSelected) Color.Black else BiaarCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Stake Amount Selector
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "قيمة الرهان التجريبي:",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "الربح المتوقع: +$formattedPossibleWin عملة",
                        color = BiaarGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    stakes.forEach { stake ->
                        val isSelected = diceState.selectedStake == stake
                        val formatted = NumberFormat.getNumberInstance(Locale.US).format(stake)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) BiaarEmerald.copy(alpha = 0.25f) else BiaarDarkCard
                                )
                                .border(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) BiaarEmerald else BiaarDarkCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setDiceStake(stake) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formatted,
                                color = if (isSelected) BiaarEmerald else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 5. High-Visibility Roll Button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Button(
                    onClick = { viewModel.rollDice() },
                    enabled = !diceState.isRolling,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("roll_dice_action_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BiaarEmerald,
                        disabledContainerColor = BiaarDarkCard
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (diceState.isRolling) "جاري رمي النرد... 🎲" else "ارمِ النرد الآن (اربح $formattedPossibleWin عملة)",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiceFaceView(
    value: Int,
    rotation: Float,
    isRolling: Boolean
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .rotate(rotation)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFF9E6), Color(0xFFFFD54F), Color(0xFFFFA000))
                )
            )
            .border(2.dp, Color(0xFFFFECB3), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(54.dp)) {
            val pipRadius = 4.5.dp.toPx()
            val pipColor = Color(0xFF1E160D)

            val left = size.width * 0.25f
            val center = size.width * 0.5f
            val right = size.width * 0.75f

            val top = size.height * 0.25f
            val middle = size.height * 0.5f
            val bottom = size.height * 0.75f

            fun drawPip(x: Float, y: Float) {
                drawCircle(color = pipColor, radius = pipRadius, center = Offset(x, y))
            }

            when (value) {
                1 -> drawPip(center, middle)
                2 -> {
                    drawPip(left, top)
                    drawPip(right, bottom)
                }
                3 -> {
                    drawPip(left, top)
                    drawPip(center, middle)
                    drawPip(right, bottom)
                }
                4 -> {
                    drawPip(left, top)
                    drawPip(right, top)
                    drawPip(left, bottom)
                    drawPip(right, bottom)
                }
                5 -> {
                    drawPip(left, top)
                    drawPip(right, top)
                    drawPip(center, middle)
                    drawPip(left, bottom)
                    drawPip(right, bottom)
                }
                6 -> {
                    drawPip(left, top)
                    drawPip(right, top)
                    drawPip(left, middle)
                    drawPip(right, middle)
                    drawPip(left, bottom)
                    drawPip(right, bottom)
                }
            }
        }
    }
}
