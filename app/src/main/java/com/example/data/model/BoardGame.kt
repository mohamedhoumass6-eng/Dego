package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

enum class GameCategory(val titleAr: String) {
    ALL("الكل"),
    CARDS("ورق وبلوت"),
    DICE_TABLE("نرد وطاولة"),
    STRATEGY("ذكاء وشطرنج"),
    CLASSIC_BOARD("ألواح وكيرم")
}

enum class GameType {
    BALOOT,
    DICE_ARENA,
    TAWLA,
    CARROM,
    CHESS,
    LUDO,
    JACKAROO
}

data class BoardGame(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val category: GameCategory,
    val gameType: GameType,
    val descriptionAr: String,
    val minBet: Long,
    val maxBet: Long,
    val defaultMultiplier: Float,
    val activePlayers: Int,
    val minPlayers: Int = 2,
    val maxPlayers: Int = 4,
    val isHot: Boolean = false,
    val isNew: Boolean = false,
    val tagAr: String = "متاح الآن",
    @DrawableRes val bannerRes: Int? = null,
    val gradientColors: List<Long> = listOf(0xFF1E293B, 0xFF0F172A)
)

data class Tournament(
    val id: String,
    val titleAr: String,
    val gameTitleAr: String,
    val prizePoolCoins: Long,
    val entryFeeCoins: Long,
    val registeredPlayers: Int,
    val maxPlayers: Int,
    val startsInAr: String,
    val statusAr: String,
    val isFeatured: Boolean = false
)
