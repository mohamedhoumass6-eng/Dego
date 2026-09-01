package com.example.data.model

enum class TransactionType(val titleAr: String, val isCredit: Boolean) {
    BET_PLACED("رهان جولة تجريبية", false),
    BET_WON("مكافأة فوز بالجولة", true),
    DAILY_GIFT("مكافأة تسجيل الدخول اليومية", true),
    DEMO_RECHARGE("شحن رصيد تجريبي مجاني", true),
    TOURNAMENT_ENTRY("رسوم دخول بطولة", false),
    TOURNAMENT_PRIZE("جائزة مركز متقدم في البطولة", true),
    REFUND("استرداد رهان الجولة الملغاة", true)
}

data class TransactionRecord(
    val id: String,
    val type: TransactionType,
    val amount: Long,
    val titleAr: String,
    val subtitleAr: String,
    val timeAgoAr: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameRoom(
    val id: String,
    val gameId: String,
    val gameTitleAr: String,
    val roomNameAr: String,
    val hostNameAr: String,
    val hostAvatarId: Int,
    val stakeAmount: Long,
    val currentPlayers: Int,
    val maxPlayers: Int,
    val isPrivate: Boolean = false,
    val statusAr: String = "في الانتظار",
    val isFastMode: Boolean = false
)
