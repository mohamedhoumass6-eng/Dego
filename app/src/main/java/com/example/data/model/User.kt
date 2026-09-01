package com.example.data.model

data class User(
    val id: String = "usr_01",
    val fullNameAr: String = "فهد السبيعي",
    val username: String = "Fahad_VIP",
    val email: String = "fahad@biaar.games",
    val phone: String = "+966 50 123 4567",
    val avatarId: Int = 1,
    val vipLevel: VipLevel = VipLevel.DIAMOND,
    val demoBalance: Long = 12500L,
    val gamesPlayed: Int = 148,
    val gamesWon: Int = 98,
    val gamesLost: Int = 50,
    val currentStreak: Int = 5,
    val bestStreak: Int = 12,
    val totalWinnings: Long = 68400L,
    val totalWagered: Long = 94200L,
    val rankAr: String = "ماستر الألواح",
    val isGuest: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val isHapticEnabled: Boolean = true,
    val selectedLanguageAr: Boolean = true
) {
    val winRate: Float
        get() = if (gamesPlayed > 0) (gamesWon.toFloat() / gamesPlayed.toFloat()) * 100f else 0f
}

enum class VipLevel(
    val titleAr: String,
    val colorHex: Long,
    val multiplierBonus: String,
    val dailyGiftBonus: Long
) {
    BRONZE("برونزي", 0xFFCD7F32, "1.0x", 500L),
    SILVER("فضي", 0xFFC0C0C0, "1.1x", 1000L),
    GOLD("ذهبي", 0xFFFFD700, "1.25x", 2500L),
    DIAMOND("ماسي VIP", 0xFF00E5FF, "1.5x", 5000L),
    LEGENDARY("أسطوري", 0xFFFF3366, "2.0x", 10000L)
}
