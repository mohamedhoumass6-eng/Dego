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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BoardGame
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

@Composable
fun CreateRoomDialog(
    games: List<BoardGame>,
    onDismiss: () -> Unit,
    onCreate: (gameId: String, gameTitleAr: String, roomName: String, stake: Long) -> Unit
) {
    if (games.isEmpty()) return

    var selectedGame by remember { mutableStateOf(games.first()) }
    var roomName by remember { mutableStateOf("طاولة نرد الأبطال #${(100..999).random()}") }
    var selectedStake by remember { mutableLongStateOf(games.first().minBet) }

    val stakeOptions = listOf(50L, 100L, 250L, 500L, 1000L, 2500L)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = BiaarDarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BiaarDarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = BiaarGoldLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إنشاء طاولة نرد جديدة",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إلغاء",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Game Selector Chips
                Text(
                    text = "اختر اللعبة:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    games.take(3).forEach { game ->
                        val isSelected = selectedGame.id == game.id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) BiaarEmerald.copy(alpha = 0.2f) else BiaarDarkCard
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) BiaarEmerald else BiaarDarkCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedGame = game
                                    selectedStake = game.minBet
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = game.titleAr.split(" ").take(2).joinToString(" "),
                                color = if (isSelected) BiaarEmerald else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Room Name Field
                Text(
                    text = "اسم الغرفة:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BiaarEmerald,
                        unfocusedBorderColor = BiaarDarkCardBorder,
                        focusedContainerColor = BiaarDarkCard,
                        unfocusedContainerColor = BiaarDarkCard
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stake Selector
                Text(
                    text = "قيمة الرهان التجريبي للجولة:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    stakeOptions.take(4).forEach { stake ->
                        val isSelected = selectedStake == stake
                        val formatted = NumberFormat.getNumberInstance(Locale.US).format(stake)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) BiaarGold.copy(alpha = 0.2f) else BiaarDarkCard
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) BiaarGold else BiaarDarkCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedStake = stake }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$formatted",
                                color = if (isSelected) BiaarGoldLight else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onCreate(selectedGame.id, selectedGame.titleAr, roomName, selectedStake)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("create_room_submit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BiaarEmerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "إنشاء الغرفة وبدء الانتظار",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
