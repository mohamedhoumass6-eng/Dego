package com.example.data.repository

import com.example.R
import com.example.data.model.BetRecord
import com.example.data.model.BetStatus
import com.example.data.model.BoardGame
import com.example.data.model.GameCategory
import com.example.data.model.GameRoom
import com.example.data.model.GameType
import com.example.data.model.Tournament
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionType
import com.example.data.model.User
import com.example.data.model.VipLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class BiaarRepository private constructor() {

    private val _currentUser = MutableStateFlow(
        User(
            id = "usr_01",
            fullNameAr = "فهد السبيعي",
            username = "Fahad_VIP",
            email = "fahad@biaar.games",
            phone = "+966 50 123 4567",
            avatarId = 1,
            vipLevel = VipLevel.DIAMOND,
            demoBalance = 15400L,
            gamesPlayed = 84,
            gamesWon = 56,
            gamesLost = 28,
            currentStreak = 4,
            bestStreak = 9,
            totalWinnings = 84200L,
            totalWagered = 112000L,
            rankAr = "ماستر النخبة"
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _games = MutableStateFlow(getInitialGames())
    val games: StateFlow<List<BoardGame>> = _games.asStateFlow()

    private val _tournaments = MutableStateFlow(getInitialTournaments())
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    private val _rooms = MutableStateFlow(getInitialRooms())
    val rooms: StateFlow<List<GameRoom>> = _rooms.asStateFlow()

    private val _betsHistory = MutableStateFlow(getInitialBets())
    val betsHistory: StateFlow<List<BetRecord>> = _betsHistory.asStateFlow()

    private val _transactions = MutableStateFlow(getInitialTransactions())
    val transactions: StateFlow<List<TransactionRecord>> = _transactions.asStateFlow()

    private val _isDailyGiftClaimable = MutableStateFlow(true)
    val isDailyGiftClaimable: StateFlow<Boolean> = _isDailyGiftClaimable.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // ----------------------------------------------------
    // User & Authentication Actions
    // ----------------------------------------------------
    fun login(emailOrPhone: String, password: String): Boolean {
        _currentUser.update {
            it.copy(
                fullNameAr = if (emailOrPhone.contains("@")) emailOrPhone.substringBefore("@") else "لاعب بيار المتميز",
                username = "VIP_Player",
                email = if (emailOrPhone.contains("@")) emailOrPhone else "player@biaar.games",
                isGuest = false
            )
        }
        _isAuthenticated.value = true
        return true
    }

    fun register(fullName: String, emailOrPhone: String, password: String): Boolean {
        _currentUser.update {
            it.copy(
                fullNameAr = fullName.ifBlank { "بطل جديد" },
                username = "Hero_${Random.nextInt(100, 999)}",
                email = emailOrPhone,
                demoBalance = 10000L,
                vipLevel = VipLevel.SILVER,
                isGuest = false
            )
        }
        _isAuthenticated.value = true
        addTransaction(
            TransactionType.DAILY_GIFT,
            10000L,
            "هدية ترحيبية للمستخدم الجديد",
            "رصيد تجريبي لبدء اللعب"
        )
        return true
    }

    fun loginAsGuest() {
        _currentUser.update {
            it.copy(
                fullNameAr = "ضيف تجريبي #${Random.nextInt(1000, 9999)}",
                username = "Guest_${Random.nextInt(100, 999)}",
                email = "guest@biaar.demo",
                demoBalance = 8000L,
                vipLevel = VipLevel.BRONZE,
                isGuest = true
            )
        }
        _isAuthenticated.value = true
    }

    fun switchDemoAccount(accountIndex: Int) {
        val demoAccounts = listOf(
            User(
                id = "usr_01",
                fullNameAr = "فهد السبيعي",
                username = "Fahad_VIP",
                email = "fahad@biaar.games",
                vipLevel = VipLevel.DIAMOND,
                demoBalance = 24500L,
                gamesPlayed = 112,
                gamesWon = 78,
                gamesLost = 34,
                avatarId = 1,
                rankAr = "ماستر النخبة"
            ),
            User(
                id = "usr_02",
                fullNameAr = "سلطان النرد",
                username = "DiceMaster_KSA",
                email = "sultan@biaar.games",
                vipLevel = VipLevel.LEGENDARY,
                demoBalance = 95000L,
                gamesPlayed = 340,
                gamesWon = 260,
                gamesLost = 80,
                avatarId = 2,
                rankAr = "أسطورة الطاولة"
            ),
            User(
                id = "usr_03",
                fullNameAr = "أميرة البلوت",
                username = "Baloot_Queen",
                email = "queen@biaar.games",
                vipLevel = VipLevel.GOLD,
                demoBalance = 18200L,
                gamesPlayed = 65,
                gamesWon = 44,
                gamesLost = 21,
                avatarId = 3,
                rankAr = "محترفة الصن والحكم"
            )
        )
        _currentUser.value = demoAccounts[accountIndex % demoAccounts.size]
        _isAuthenticated.value = true
    }

    fun logout() {
        _isAuthenticated.value = false
    }

    fun updateProfileSettings(sound: Boolean, haptic: Boolean, avatarId: Int? = null) {
        _currentUser.update {
            it.copy(
                isSoundEnabled = sound,
                isHapticEnabled = haptic,
                avatarId = avatarId ?: it.avatarId
            )
        }
    }

    // ----------------------------------------------------
    // Wallet & Demo Balance Operations
    // ----------------------------------------------------
    fun rechargeDemoBalance(amount: Long) {
        _currentUser.update { it.copy(demoBalance = it.demoBalance + amount) }
        addTransaction(
            TransactionType.DEMO_RECHARGE,
            amount,
            "شحن رصيد تجريبي مجاني",
            "إضافة رصيد وهمي للمنصة"
        )
    }

    fun claimDailyGift(): Long {
        if (!_isDailyGiftClaimable.value) return 0L
        val bonus = _currentUser.value.vipLevel.dailyGiftBonus
        _currentUser.update {
            it.copy(
                demoBalance = it.demoBalance + bonus,
                currentStreak = it.currentStreak + 1
            )
        }
        _isDailyGiftClaimable.value = false
        addTransaction(
            TransactionType.DAILY_GIFT,
            bonus,
            "مكافأة تسجيل الدخول اليومية",
            "مكافأة المستوى ${_currentUser.value.vipLevel.titleAr}"
        )
        return bonus
    }

    private fun addTransaction(
        type: TransactionType,
        amount: Long,
        titleAr: String,
        subtitleAr: String
    ) {
        val newTx = TransactionRecord(
            id = "tx_${System.currentTimeMillis()}",
            type = type,
            amount = amount,
            titleAr = titleAr,
            subtitleAr = subtitleAr,
            timeAgoAr = "الآن"
        )
        _transactions.update { listOf(newTx) + it }
    }

    // ----------------------------------------------------
    // Game Match & Bet Processing
    // ----------------------------------------------------
    fun placeBetAndPlay(
        gameId: String,
        gameTitleAr: String,
        stake: Long,
        isWin: Boolean,
        multiplier: Float,
        matchDetailsAr: String,
        opponentName: String = "لاعب بيار المنافس"
    ): BetRecord {
        val payout = if (isWin) (stake * multiplier).toLong() else 0L
        val status = if (isWin) BetStatus.WON else BetStatus.LOST

        // Deduct stake first
        _currentUser.update { user ->
            val updatedBalance = if (isWin) {
                user.demoBalance - stake + payout
            } else {
                user.demoBalance - stake
            }
            val newWins = if (isWin) user.gamesWon + 1 else user.gamesWon
            val newLoss = if (!isWin) user.gamesLost + 1 else user.gamesLost
            val newStreak = if (isWin) user.currentStreak + 1 else 0
            val bestStreak = maxOf(user.bestStreak, newStreak)
            val addedWin = if (isWin) payout - stake else 0L

            user.copy(
                demoBalance = maxOf(0L, updatedBalance),
                gamesPlayed = user.gamesPlayed + 1,
                gamesWon = newWins,
                gamesLost = newLoss,
                currentStreak = newStreak,
                bestStreak = bestStreak,
                totalWinnings = user.totalWinnings + addedWin,
                totalWagered = user.totalWagered + stake
            )
        }

        // Add to bet history
        val newBet = BetRecord(
            id = "bet_${System.currentTimeMillis()}",
            gameId = gameId,
            gameTitleAr = gameTitleAr,
            roomId = "ROOM-${Random.nextInt(100, 999)}",
            stakeAmount = stake,
            multiplier = multiplier,
            payoutAmount = payout,
            status = status,
            opponentNameAr = opponentName,
            opponentAvatarId = Random.nextInt(1, 5),
            matchDetailsAr = matchDetailsAr,
            timeAgoAr = "الآن"
        )
        _betsHistory.update { listOf(newBet) + it }

        // Record transactions
        addTransaction(
            TransactionType.BET_PLACED,
            stake,
            "رهان: $gameTitleAr",
            "حجز رصيد الجولة"
        )
        if (isWin) {
            addTransaction(
                TransactionType.BET_WON,
                payout,
                "فوز بجولة $gameTitleAr",
                "أرباح بمضاعف $multiplier x"
            )
        }

        return newBet
    }

    fun createRoom(gameId: String, gameTitleAr: String, roomName: String, stake: Long): GameRoom {
        val newRoom = GameRoom(
            id = "rm_${System.currentTimeMillis()}",
            gameId = gameId,
            gameTitleAr = gameTitleAr,
            roomNameAr = roomName.ifBlank { "غرفة ${currentUser.value.fullNameAr}" },
            hostNameAr = currentUser.value.fullNameAr,
            hostAvatarId = currentUser.value.avatarId,
            stakeAmount = stake,
            currentPlayers = 1,
            maxPlayers = 4,
            statusAr = "في الانتظار"
        )
        _rooms.update { listOf(newRoom) + it }
        return newRoom
    }

    // ----------------------------------------------------
    // ----------------------------------------------------
    // Initial Seed Data
    // ----------------------------------------------------
    private fun getInitialGames(): List<BoardGame> {
        return listOf(
            BoardGame(
                id = "game_dice",
                titleAr = "لعبة نرد LGDAH الذهبي",
                titleEn = "LGDAH Golden Dice",
                category = GameCategory.DICE_TABLE,
                gameType = GameType.DICE_ARENA,
                descriptionAr = "رمي النرد التنافسي التفاعلي مع جولات سريعة، رهانات مرنة ومضاعفات ربح فورية حتى 5x.",
                minBet = 50L,
                maxBet = 10000L,
                defaultMultiplier = 1.95f,
                activePlayers = 5820,
                isHot = true,
                tagAr = "اللعبة الحصرية 🎲",
                bannerRes = R.drawable.tawla_banner,
                gradientColors = listOf(0xFF3B2B1B, 0xFF1E160D)
            )
        )
    }

    private fun getInitialTournaments(): List<Tournament> {
        return listOf(
            Tournament(
                id = "tour_01",
                titleAr = "بطولة كأس نرد LGDAH الكبرى",
                gameTitleAr = "لعبة نرد LGDAH الذهبي",
                prizePoolCoins = 100000L,
                entryFeeCoins = 500L,
                registeredPlayers = 112,
                maxPlayers = 128,
                startsInAr = "خلال 15 دقيقة",
                statusAr = "التسجيل مفتوح",
                isFeatured = true
            ),
            Tournament(
                id = "tour_02",
                titleAr = "تحدي النرد الخاطف السريع",
                gameTitleAr = "لعبة نرد LGDAH الذهبي",
                prizePoolCoins = 40000L,
                entryFeeCoins = 250L,
                registeredPlayers = 56,
                maxPlayers = 64,
                startsInAr = "خلال ساعة",
                statusAr = "التسجيل مفتوح"
            )
        )
    }

    private fun getInitialRooms(): List<GameRoom> {
        return listOf(
            GameRoom(
                id = "rm_101",
                gameId = "game_dice",
                gameTitleAr = "لعبة نرد LGDAH الذهبي",
                roomNameAr = "طاولة نرد المحترفين (رهان 500)",
                hostNameAr = "سلطان النرد",
                hostAvatarId = 2,
                stakeAmount = 500L,
                currentPlayers = 1,
                maxPlayers = 2,
                statusAr = "بانتظار المنافس",
                isFastMode = true
            ),
            GameRoom(
                id = "rm_102",
                gameId = "game_dice",
                gameTitleAr = "لعبة نرد LGDAH الذهبي",
                roomNameAr = "مبارزة نرد 1 ضد 1 سريعة",
                hostNameAr = "صقر الخليج",
                hostAvatarId = 1,
                stakeAmount = 250L,
                currentPlayers = 1,
                maxPlayers = 2,
                statusAr = "بانتظار المنافس"
            ),
            GameRoom(
                id = "rm_103",
                gameId = "game_dice",
                gameTitleAr = "لعبة نرد LGDAH الذهبي",
                roomNameAr = "ديوانية النرد VIP (رهان 1,000)",
                hostNameAr = "بطل LGDAH",
                hostAvatarId = 3,
                stakeAmount = 1000L,
                currentPlayers = 1,
                maxPlayers = 2,
                statusAr = "VIP مفتوحة"
            )
        )
    }

    private fun getInitialBets(): List<BetRecord> {
        return listOf(
            BetRecord(
                id = "bet_101",
                gameId = "game_dice",
                gameTitleAr = "لعبة نرد LGDAH الذهبي",
                roomId = "ROOM-882",
                stakeAmount = 500L,
                multiplier = 3.5f,
                payoutAmount = 1750L,
                status = BetStatus.WON,
                opponentNameAr = "صقر الخليج",
                opponentAvatarId = 2,
                matchDetailsAr = "فوز بتوقع دبل نرد ناجح (5-5)",
                timeAgoAr = "منذ 10 دقائق"
            ),
            BetRecord(
                id = "bet_102",
                gameId = "game_dice",
                gameTitleAr = "لعبة نرد LGDAH الذهبي",
                roomId = "ROOM-409",
                stakeAmount = 250L,
                multiplier = 1.95f,
                payoutAmount = 487L,
                status = BetStatus.WON,
                opponentNameAr = "سلطان النرد",
                opponentAvatarId = 3,
                matchDetailsAr = "توقع مجموع عالي (أكثر من 7)",
                timeAgoAr = "منذ 25 دقيقة"
            ),
            BetRecord(
                id = "bet_103",
                gameId = "game_dice",
                gameTitleAr = "لعبة نرد LGDAH الذهبي",
                roomId = "ROOM-312",
                stakeAmount = 300L,
                multiplier = 2.0f,
                payoutAmount = 0L,
                status = BetStatus.LOST,
                opponentNameAr = "خالد الدوسري",
                opponentAvatarId = 4,
                matchDetailsAr = "توقع مجموع منخفض - النتيجة 9",
                timeAgoAr = "منذ ساعة"
            )
        )
    }

    private fun getInitialTransactions(): List<TransactionRecord> {
        return listOf(
            TransactionRecord(
                id = "tx_01",
                type = TransactionType.BET_WON,
                amount = 1750L,
                titleAr = "فوز بجولة نرد LGDAH",
                subtitleAr = "جائزة جولة طاولة #ROOM-882",
                timeAgoAr = "منذ 10 دقائق"
            ),
            TransactionRecord(
                id = "tx_02",
                type = TransactionType.BET_PLACED,
                amount = 500L,
                titleAr = "رهان جولة نرد LGDAH",
                subtitleAr = "حجز رصيد المراهنة التجريبية",
                timeAgoAr = "منذ 15 دقيقة"
            ),
            TransactionRecord(
                id = "tx_03",
                type = TransactionType.DAILY_GIFT,
                amount = 5000L,
                titleAr = "مكافأة تسجيل الدخول اليومية",
                subtitleAr = "مكافأة رتبة VIP الماسية",
                timeAgoAr = "اليوم 8:00 ص"
            ),
            TransactionRecord(
                id = "tx_04",
                type = TransactionType.DEMO_RECHARGE,
                amount = 10000L,
                titleAr = "شحن رصيد تجريبي مجاني",
                subtitleAr = "إضافة عملات تجريبية للمحفظة",
                timeAgoAr = "أمس"
            )
        )
    }

    companion object {
        @Volatile
        private var instance: BiaarRepository? = null

        fun getInstance(): BiaarRepository {
            return instance ?: synchronized(this) {
                instance ?: BiaarRepository().also { instance = it }
            }
        }
    }
}
