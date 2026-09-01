package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BiaarDarkCardBorder
import com.example.ui.theme.BiaarDarkSurface
import com.example.ui.theme.BiaarDarkSurfaceVariant
import com.example.ui.theme.BiaarEmerald
import com.example.ui.theme.BiaarGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.BiaarNavTab

@Composable
fun BiaarBottomNav(
    currentTab: BiaarNavTab,
    onTabSelected: (BiaarNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BiaarDarkSurface.copy(alpha = 0.95f),
                        BiaarDarkSurface
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(BiaarDarkCardBorder, Color.Transparent)
                ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                tab = BiaarNavTab.HOME,
                isSelected = currentTab == BiaarNavTab.HOME,
                icon = Icons.Default.Home,
                onClick = { onTabSelected(BiaarNavTab.HOME) }
            )

            NavTabItem(
                tab = BiaarNavTab.BETS_HISTORY,
                isSelected = currentTab == BiaarNavTab.BETS_HISTORY,
                icon = Icons.Default.History,
                onClick = { onTabSelected(BiaarNavTab.BETS_HISTORY) }
            )

            // Center Play Room Highlighted Button for Dice Game
            PlayRoomCenterButton(
                isSelected = currentTab == BiaarNavTab.PLAY_ROOM,
                onClick = { onTabSelected(BiaarNavTab.PLAY_ROOM) }
            )

            NavTabItem(
                tab = BiaarNavTab.WALLET,
                isSelected = currentTab == BiaarNavTab.WALLET,
                icon = Icons.Default.AccountBalanceWallet,
                onClick = { onTabSelected(BiaarNavTab.WALLET) }
            )

            NavTabItem(
                tab = BiaarNavTab.PROFILE,
                isSelected = currentTab == BiaarNavTab.PROFILE,
                icon = Icons.Default.Person,
                onClick = { onTabSelected(BiaarNavTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    tab: BiaarNavTab,
    isSelected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) BiaarEmerald else TextMuted,
        label = "nav_icon_color"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) TextPrimary else TextMuted,
        label = "nav_text_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("nav_tab_${tab.route}")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.titleAr,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = tab.titleAr,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(width = 14.dp, height = 3.dp)
                    .clip(CircleShape)
                    .background(BiaarEmerald)
            )
        }
    }
}

@Composable
private fun PlayRoomCenterButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("nav_tab_play_room")
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (isSelected) {
                            listOf(BiaarGold, Color(0xFFC68A00))
                        } else {
                            listOf(BiaarEmerald, Color(0xFF00A844))
                        }
                    )
                )
                .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = "صالة اللعب",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "العب الآن",
            color = if (isSelected) BiaarGold else BiaarEmerald,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
