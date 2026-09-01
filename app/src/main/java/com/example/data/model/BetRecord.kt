package com.example.data.model

enum class BetStatus(val titleAr: String, val colorHex: Long) {
    WON("ربح", 0xFF00E676),
    LOST("خسارة", 0xFFFF3366),
    ACTIVE("قيد اللعب", 0xFFFFB800),
    CANCELLED("مسترد", 0xFF94A3B8)
}

data class BetRecord(
    val id: String,
    val gameId: String,
    val gameTitleAr: String,
    val roomId: String,
    val stakeAmount: Long,
    val multiplier: Float,
    val payoutAmount: Long,
    val status: BetStatus,
    val opponentNameAr: String,
    val opponentAvatarId: Int,
    val matchDetailsAr: String,
    val timeAgoAr: String,
    val timestamp: Long = System.currentTimeMillis()
)
