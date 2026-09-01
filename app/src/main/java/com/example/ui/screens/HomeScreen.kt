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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BoardGame
import com.example.data.model.GameRoom
import com.example.data.model.Tournament
import com.example.data.model.User
import com.example.ui.components.DemoNoticeBanner
import com.example.ui.theme.BiaarCyan
import com.example.ui.theme.BiaarDarkCard
import com.example.ui.theme.BiaarDarkCardBorder
import com.example.ui.theme.BiaarDarkSurface
import com.example.ui.theme.BiaarEmerald
import com.example.ui.theme.BiaarEmeraldLight
import com.example.ui.theme.BiaarGold
import com.example.ui.theme.BiaarGoldLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BiaarNavTab
import com.example.ui.viewmodel.BiaarViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: BiaarViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val games by viewModel.games.collectAsState()
    val tournaments by viewModel.tournaments.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val isDailyGiftClaimable by viewModel.isDailyGiftClaimable.collectAsState()

    val diceGame = games.firstOrNull()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Top Mini Bar with App Name DéGo and Status
        item {
            DegoAppHeader()
        }

        // 2. Center Prominent Hero Banner: Large, Elegant "LGDAH" Title
        item {
            LgdahCenterHeroTitle(
                onEnterDiceGame = {
                    diceGame?.let { viewModel.openPlayRoomForGame(it) }
                        ?: viewModel.selectTab(BiaarNavTab.PLAY_ROOM)
                }
            )
        }

        // 3. Demo Disclaimer Notice
        item {
            DemoNoticeBanner(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                customText = "🎮 تطبيق DéGo مخصص للترفيه باستخدام عملات ورصيد تجريبي فقط"
            )
        }

        // 4. Bento Wallet & Quick Action Controls
        item {
            BentoQuickActionsRow(
                user = user,
                isGiftClaimable = isDailyGiftClaimable,
                onRecharge = { viewModel.setRechargeSheetVisible(true) },
                onClaimGift = { viewModel.claimDailyGift() },
                onOpenWallet = { viewModel.selectTab(BiaarNavTab.WALLET) },
                onQuickPlay = {
                    diceGame?.let { viewModel.openPlayRoomForGame(it) }
                        ?: viewModel.selectTab(BiaarNavTab.PLAY_ROOM)
                }
            )
        }

        // 5. Dice Tournament Spotlight Banner
        item {
            tournaments.firstOrNull()?.let { tournament ->
                LgdahTournamentBanner(
                    tournament = tournament,
                    onJoinTournament = {
                        diceGame?.let { viewModel.openPlayRoomForGame(it) }
                            ?: viewModel.selectTab(BiaarNavTab.PLAY_ROOM)
                    }
                )
            }
        }

        // 6. Open Dice Tables & Rooms
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                titleAr = "طاولات النرد المفتوحة",
                subtitleAr = "انضم لطاولات التحدي المباشرة ونافس على النرد",
                actionAr = "إنشاء طاولة +",
                onActionClick = { viewModel.setCreateRoomDialogVisible(true) }
            )
        }

        items(rooms) { room ->
            DiceRoomItem(
                room = room,
                onJoin = {
                    diceGame?.let { viewModel.openPlayRoomForGame(it) }
                        ?: viewModel.selectTab(BiaarNavTab.PLAY_ROOM)
                }
            )
        }

        // 7. Recent Winners Feed
        item {
            Spacer(modifier = Modifier.height(12.dp))
            DiceRecentWinnersCard()
        }
    }
}

@Composable
private fun DegoAppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "تطبيق",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "DéGo",
                color = BiaarGoldLight,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(BiaarEmerald.copy(alpha = 0.15f))
                .border(1.dp, BiaarEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(BiaarEmerald)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "الخوادم متصلة 🟢",
                    color = BiaarEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Prominent, large, elegant LGDAH center title hero card.
 * Designed with modern aesthetic, refined typography, and a clear button to access the dedicated Dice Game page.
 */
@Composable
private fun LgdahCenterHeroTitle(
    onEnterDiceGame: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(1.5.dp, BiaarGold.copy(alpha = 0.55f), RoundedCornerShape(24.dp))
            .testTag("lgdah_center_hero_card"),
        colors = CardDefaults.cardColors(containerColor = BiaarDarkCard),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Background Artwork
            Image(
                painter = painterResource(id = R.drawable.tawla_banner),
                contentDescription = "LGDAH Dice Arena",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )

            // Multi-stop gradient for deep, luxurious contrast and high readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.65f),
                                Color(0xEB0E1524),
                                BiaarDarkCard
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(BiaarGold, BiaarGoldLight)
                                )
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🎲",
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "الساحة الرسمية",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = BiaarCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "1,420 لاعب نشط",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // LARGE, ELEGANT, CLEAR "LGDAH" TITLE
                Text(
                    text = "LGDAH",
                    color = BiaarGoldLight,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("lgdah_main_hero_title")
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "منصة لعبة النرد والتحديات الحماسية",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "ارمي النردين في الصفحة المستقلة، اختر توقعك وضاعف رصيدك حتى 4.8x!",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // CLEAR ENTRY BUTTON TO DEDICATED DICE GAME SCREEN
                Button(
                    onClick = onEnterDiceGame,
                    colors = ButtonDefaults.buttonColors(containerColor = BiaarEmerald),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("enter_dice_game_button"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الدخول إلى لعبة النرد 🎲 (صفحة مستقلة)",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun BentoQuickActionsRow(
    user: User,
    isGiftClaimable: Boolean,
    onRecharge: () -> Unit,
    onClaimGift: () -> Unit,
    onOpenWallet: () -> Unit,
    onQuickPlay: () -> Unit
) {
    val formattedBalance = NumberFormat.getNumberInstance(Locale.US).format(user.demoBalance)
    val giftBonus = NumberFormat.getNumberInstance(Locale.US).format(user.vipLevel.dailyGiftBonus)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Bento Main Wallet Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BiaarDarkCardBorder, RoundedCornerShape(20.dp))
                .clickable(onClick = onOpenWallet)
                .testTag("bento_wallet_card"),
            colors = CardDefaults.cardColors(containerColor = BiaarDarkCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF182236), Color(0xFF0F1624))
                        )
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(BiaarGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = BiaarGoldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "الرصيد التجريبي المجاني",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = formattedBalance,
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "عملة",
                                color = BiaarGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onRecharge,
                        colors = ButtonDefaults.buttonColors(containerColor = BiaarEmerald),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("home_quick_recharge_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
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

        // Bento 2-Col Split: Quick Match & Daily Gift
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Quick Dice Roll Tile
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, BiaarEmerald.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .clickable(onClick = onQuickPlay)
                    .testTag("bento_quick_play_tile"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0E221D)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BiaarEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = BiaarEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BiaarEmerald)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "فوري",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "رمية سريعة 🎲",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "دخول لعبة النرد",
                        color = BiaarEmeraldLight,
                        fontSize = 11.sp
                    )
                }
            }

            // Daily Reward / VIP Status Tile
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        1.dp,
                        if (isGiftClaimable) BiaarGold.copy(alpha = 0.5f) else BiaarDarkCardBorder,
                        RoundedCornerShape(18.dp)
                    )
                    .clickable(onClick = if (isGiftClaimable) onClaimGift else onOpenWallet)
                    .testTag("bento_daily_reward_tile"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isGiftClaimable) Color(0xFF281C09) else BiaarDarkCard
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BiaarGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = BiaarGoldLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isGiftClaimable) BiaarGold else Color(0xFF223048))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isGiftClaimable) "جاهزة 🎁" else "تم الاستلام",
                                color = if (isGiftClaimable) Color.Black else TextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "مكافأة يومية",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isGiftClaimable) "+$giftBonus عملة مجاناً" else "مستوى ${user.vipLevel.titleAr}",
                        color = BiaarGoldLight,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LgdahTournamentBanner(
    tournament: Tournament,
    onJoinTournament: () -> Unit
) {
    val prizeFormatted = NumberFormat.getNumberInstance(Locale.US).format(tournament.prizePoolCoins)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BiaarGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.tournament_banner),
            contentDescription = "Tournament",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        // Dark gradient overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.92f),
                            Color.Black.copy(alpha = 0.45f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BiaarGold)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🏆 بطولة نرد LGDAH الكبرى",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = tournament.startsInAr,
                    color = BiaarCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = tournament.titleAr,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "مجموع الجوائز: ",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$prizeFormatted عملة تجريبية",
                        color = BiaarGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المسجلون: ${tournament.registeredPlayers}/${tournament.maxPlayers}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Button(
                    onClick = onJoinTournament,
                    colors = ButtonDefaults.buttonColors(containerColor = BiaarEmerald),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("hero_join_tournament_button")
                ) {
                    Text(
                        text = "انضم للبطولة",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DiceRoomItem(
    room: GameRoom,
    onJoin: () -> Unit
) {
    val formattedStake = NumberFormat.getNumberInstance(Locale.US).format(room.stakeAmount)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(1.dp, BiaarDarkCardBorder, RoundedCornerShape(16.dp))
            .testTag("live_room_card_${room.id}"),
        colors = CardDefaults.cardColors(containerColor = BiaarDarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, BiaarCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = null,
                        tint = BiaarGoldLight,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = room.roomNameAr,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "طاولة نرد • ",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "رهان $formattedStake",
                            color = BiaarGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${room.currentPlayers}/${room.maxPlayers}",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onJoin,
                    colors = ButtonDefaults.buttonColors(containerColor = BiaarEmerald),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "دخول",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DiceRecentWinnersCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, BiaarGold.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = BiaarDarkCard),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(BiaarGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = BiaarGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "سجل فائزي النرد المباشر",
                        color = BiaarGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BiaarEmerald.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "مباشر 🟢",
                        color = BiaarEmerald,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val winners = listOf(
                Triple("سلطان النرد", "فاز بـ 2,400 عملة في توقع (عالي 8-12)", "منذ دقيقة"),
                Triple("فهد القحطاني", "فاز بـ 4,800 عملة في توقع (رقم الحظ 7)", "منذ 4 دقائق"),
                Triple("صقر الطاولة", "فاز بـ 3,500 عملة في توقع (Double مزدوج)", "منذ 8 دقائق")
            )

            winners.forEachIndexed { index, (name, desc, time) ->
                if (index > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF111827))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(BiaarEmerald)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = name,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = desc,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Text(
                        text = time,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    titleAr: String,
    subtitleAr: String,
    actionAr: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = titleAr,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitleAr,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        if (actionAr != null && onActionClick != null) {
            Text(
                text = actionAr,
                color = BiaarEmerald,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onActionClick)
                    .padding(4.dp)
            )
        }
    }
}
