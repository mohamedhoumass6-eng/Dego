package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BetRecord
import com.example.data.model.BoardGame
import com.example.data.model.GameCategory
import com.example.data.model.GameRoom
import com.example.data.model.GameType
import com.example.data.model.Tournament
import com.example.data.model.TransactionRecord
import com.example.data.model.User
import com.example.data.repository.BiaarRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class BiaarNavTab(val titleAr: String, val route: String) {
    HOME("الرئيسية", "home"),
    PLAY_ROOM("لعبة النرد", "play_room"),
    BETS_HISTORY("المراهنات", "bets_history"),
    WALLET("المحفظة", "wallet"),
    PROFILE("الملف", "profile")
}

enum class DicePrediction(val titleAr: String, val multiplier: Float, val descAr: String) {
    HIGH("مجموع عالي (8-12)", 1.95f, "مجموع النردين من 8 إلى 12"),
    LOW("مجموع منخفض (2-6)", 1.95f, "مجموع النردين من 2 إلى 6"),
    LUCKY_SEVEN("رقم الحظ (7)", 4.80f, "مجموع النردين يساوي 7 تماماً"),
    DOUBLE("نرد مزدوج (Double)", 3.50f, "رقمين متطابقين في النردين")
}

data class DiceGameState(
    val isRolling: Boolean = false,
    val dice1: Int = 4,
    val dice2: Int = 3,
    val selectedStake: Long = 100L,
    val selectedPrediction: DicePrediction = DicePrediction.HIGH,
    val lastResultWin: Boolean? = null,
    val lastWinAmount: Long = 0L,
    val roundCount: Int = 1,
    val playerRoundScore: Int = 0,
    val botRoundScore: Int = 0,
    val botNameAr: String = "صقر الطاولة 🤖",
    val statusMessageAr: String = "اختر رهانك وتوقعك ثم ارمِ النرد!"
)

data class BalootCard(
    val suitAr: String, // سبيت, كبة, ديمن, شريا
    val rankAr: String, // A, K, Q, J, 10, 9, 8, 7
    val value: Int,
    val symbol: String
)

data class BalootGameState(
    val isPlaying: Boolean = false,
    val gameTypeAr: String = "صن", // صن أو حكم
    val selectedStake: Long = 250L,
    val userCards: List<BalootCard> = emptyList(),
    val tableCards: List<Pair<String, BalootCard>> = emptyList(), // Player name to card
    val ourScore: Int = 0,
    val theirScore: Int = 0,
    val currentRound: Int = 1,
    val statusMessageAr: String = "وزع الورق وابدأ جولة الصن الملكية!",
    val isGameFinished: Boolean = false,
    val isWin: Boolean = false,
    val winAmount: Long = 0L
)

class BiaarViewModel(
    private val repository: BiaarRepository = BiaarRepository.getInstance()
) : ViewModel() {

    val currentUser: StateFlow<User> = repository.currentUser
    val games: StateFlow<List<BoardGame>> = repository.games
    val tournaments: StateFlow<List<Tournament>> = repository.tournaments
    val rooms: StateFlow<List<GameRoom>> = repository.rooms
    val betsHistory: StateFlow<List<BetRecord>> = repository.betsHistory
    val transactions: StateFlow<List<TransactionRecord>> = repository.transactions
    val isDailyGiftClaimable: StateFlow<Boolean> = repository.isDailyGiftClaimable
    val isAuthenticated: StateFlow<Boolean> = repository.isAuthenticated

    // Navigation State
    private val _currentTab = MutableStateFlow(BiaarNavTab.HOME)
    val currentTab: StateFlow<BiaarNavTab> = _currentTab.asStateFlow()

    // Filter & Search States
    private val _selectedGameCategory = MutableStateFlow(GameCategory.ALL)
    val selectedGameCategory: StateFlow<GameCategory> = _selectedGameCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredGames: StateFlow<List<BoardGame>> = combine(
        games,
        _selectedGameCategory,
        _searchQuery
    ) { allGames, category, query ->
        allGames.filter { game ->
            val matchCategory = category == GameCategory.ALL || game.category == category
            val matchQuery = query.isBlank() ||
                    game.titleAr.contains(query, ignoreCase = true) ||
                    game.titleEn.contains(query, ignoreCase = true)
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Game in Play Room
    private val _activePlayGame = MutableStateFlow<BoardGame?>(null)
    val activePlayGame: StateFlow<BoardGame?> = _activePlayGame.asStateFlow()

    // Interactive Games State
    private val _diceGameState = MutableStateFlow(DiceGameState())
    val diceGameState: StateFlow<DiceGameState> = _diceGameState.asStateFlow()

    private val _balootGameState = MutableStateFlow(BalootGameState())
    val balootGameState: StateFlow<BalootGameState> = _balootGameState.asStateFlow()

    // Dialog & Sheets State
    private val _selectedGameForDetails = MutableStateFlow<BoardGame?>(null)
    val selectedGameForDetails: StateFlow<BoardGame?> = _selectedGameForDetails.asStateFlow()

    private val _showRechargeSheet = MutableStateFlow(false)
    val showRechargeSheet: StateFlow<Boolean> = _showRechargeSheet.asStateFlow()

    private val _showCreateRoomDialog = MutableStateFlow(false)
    val showCreateRoomDialog: StateFlow<Boolean> = _showCreateRoomDialog.asStateFlow()

    private val _showBetHistoryFilter = MutableStateFlow("ALL") // ALL, WON, LOST, ACTIVE
    val showBetHistoryFilter: StateFlow<String> = _showBetHistoryFilter.asStateFlow()

    // Notification / Toast Message
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        // Default active game is Dice Arena
        val initialDiceGame = repository.games.value.find { it.gameType == GameType.DICE_ARENA }
        _activePlayGame.value = initialDiceGame
        initBalootGame()
    }

    fun selectTab(tab: BiaarNavTab) {
        _currentTab.value = tab
    }

    fun setGameCategory(category: GameCategory) {
        _selectedGameCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectGameForDetails(game: BoardGame?) {
        _selectedGameForDetails.value = game
    }

    fun openPlayRoomForGame(game: BoardGame) {
        _activePlayGame.value = game
        _currentTab.value = BiaarNavTab.PLAY_ROOM
        _selectedGameForDetails.value = null
    }

    fun setRechargeSheetVisible(visible: Boolean) {
        _showRechargeSheet.value = visible
    }

    fun setCreateRoomDialogVisible(visible: Boolean) {
        _showCreateRoomDialog.value = visible
    }

    fun setBetHistoryFilter(filter: String) {
        _showBetHistoryFilter.value = filter
    }

    // ----------------------------------------------------
    // User / Auth
    // ----------------------------------------------------
    fun login(email: String, pass: String) {
        repository.login(email, pass)
        viewModelScope.launch {
            _toastMessage.emit("مرحباً بعودتك إلى بيار! 🎲")
        }
    }

    fun register(name: String, email: String, pass: String) {
        repository.register(name, email, pass)
        viewModelScope.launch {
            _toastMessage.emit("تم إنشاء حسابك بنجاح وحصلت على 10,000 عملة تجريبية! 🎉")
        }
    }

    fun loginAsGuest() {
        repository.loginAsGuest()
        viewModelScope.launch {
            _toastMessage.emit("تم الدخول بحساب ضيف تجريبي 🎮")
        }
    }

    fun switchDemoAccount(index: Int) {
        repository.switchDemoAccount(index)
        viewModelScope.launch {
            _toastMessage.emit("تم التبديل إلى الحساب التجريبي بنجاح")
        }
    }

    fun logout() {
        repository.logout()
    }

    fun updateSettings(sound: Boolean, haptic: Boolean, avatarId: Int? = null) {
        repository.updateProfileSettings(sound, haptic, avatarId)
    }

    // ----------------------------------------------------
    // Wallet
    // ----------------------------------------------------
    fun rechargeDemoBalance(amount: Long) {
        repository.rechargeDemoBalance(amount)
        _showRechargeSheet.value = false
        viewModelScope.launch {
            _toastMessage.emit("تمت إضافة $amount عملة تجريبية إلى محفظتك بنجاح! 💰")
        }
    }

    fun claimDailyGift() {
        val claimed = repository.claimDailyGift()
        if (claimed > 0) {
            viewModelScope.launch {
                _toastMessage.emit("مبروك! حصلت على $claimed عملة تجريبية يومية 🎁")
            }
        }
    }

    // ----------------------------------------------------
    // Create Room
    // ----------------------------------------------------
    fun createRoom(gameId: String, titleAr: String, roomName: String, stake: Long) {
        if (currentUser.value.demoBalance < stake) {
            viewModelScope.launch {
                _toastMessage.emit("رصيدك التجريبي غير كافٍ. يمكنك شحن رصيد تجريبي مجاني!")
            }
            return
        }
        val room = repository.createRoom(gameId, titleAr, roomName, stake)
        _showCreateRoomDialog.value = false
        viewModelScope.launch {
            _toastMessage.emit("تم إنشاء الغرفة (${room.roomNameAr}) بنجاح! 🏆")
        }
    }

    // ----------------------------------------------------
    // Interactive Dice Game Logic
    // ----------------------------------------------------
    fun setDiceStake(stake: Long) {
        _diceGameState.update { it.copy(selectedStake = stake) }
    }

    fun setDicePrediction(prediction: DicePrediction) {
        _diceGameState.update { it.copy(selectedPrediction = prediction) }
    }

    fun rollDice() {
        val current = _diceGameState.value
        val userBalance = currentUser.value.demoBalance

        if (userBalance < current.selectedStake) {
            viewModelScope.launch {
                _toastMessage.emit("رصيدك التجريبي غير كافٍ! اشحن رصيداً تجريبياً من المحفظة.")
            }
            return
        }

        if (current.isRolling) return

        _diceGameState.update {
            it.copy(
                isRolling = true,
                statusMessageAr = "جاري رمي النرد في الساحة... 🎲"
            )
        }

        viewModelScope.launch {
            // Dice roll animation ticks
            for (i in 1..6) {
                delay(80)
                _diceGameState.update {
                    it.copy(
                        dice1 = Random.nextInt(1, 7),
                        dice2 = Random.nextInt(1, 7)
                    )
                }
            }

            val finalD1 = Random.nextInt(1, 7)
            val finalD2 = Random.nextInt(1, 7)
            val sum = finalD1 + finalD2
            val isDouble = finalD1 == finalD2

            val isWin = when (current.selectedPrediction) {
                DicePrediction.HIGH -> sum in 8..12
                DicePrediction.LOW -> sum in 2..6
                DicePrediction.LUCKY_SEVEN -> sum == 7
                DicePrediction.DOUBLE -> isDouble
            }

            val multiplier = current.selectedPrediction.multiplier
            val winAmount = if (isWin) (current.selectedStake * multiplier).toLong() else 0L

            val matchDetails = "الرمية: ($finalD1 + $finalD2 = $sum) - التوقع: ${current.selectedPrediction.titleAr}"

            // Record to repository
            repository.placeBetAndPlay(
                gameId = "game_dice",
                gameTitleAr = "لعبة نرد LGDAH الذهبي",
                stake = current.selectedStake,
                isWin = isWin,
                multiplier = multiplier,
                matchDetailsAr = matchDetails,
                opponentName = current.botNameAr
            )

            val newPlayerScore = if (isWin) current.playerRoundScore + 1 else current.playerRoundScore
            val newBotScore = if (!isWin) current.botRoundScore + 1 else current.botRoundScore

            val statusMsg = if (isWin) {
                "مبروك! فزت بـ $winAmount عملة تجريبية! 🎉 ($finalD1 + $finalD2 = $sum)"
            } else {
                "حظ أوفر في الجولة القادمة! النتيجة: $sum"
            }

            _diceGameState.update {
                it.copy(
                    isRolling = false,
                    dice1 = finalD1,
                    dice2 = finalD2,
                    lastResultWin = isWin,
                    lastWinAmount = winAmount,
                    roundCount = it.roundCount + 1,
                    playerRoundScore = newPlayerScore,
                    botRoundScore = newBotScore,
                    statusMessageAr = statusMsg
                )
            }

            if (isWin) {
                _toastMessage.emit("ربح تجريبي: +$winAmount عملة! 🏆")
            }
        }
    }

    // ----------------------------------------------------
    // Interactive Baloot Game Logic
    // ----------------------------------------------------
    private fun initBalootGame() {
        val sampleCards = listOf(
            BalootCard("سبيت", "A", 11, "♠"),
            BalootCard("سبيت", "K", 4, "♠"),
            BalootCard("كبة", "10", 10, "♥"),
            BalootCard("ديمن", "J", 20, "♦"),
            BalootCard("شريا", "Q", 3, "♣")
        )
        _balootGameState.value = BalootGameState(
            userCards = sampleCards,
            ourScore = 0,
            theirScore = 0,
            statusMessageAr = "اختر نوع اللعب (صن أو حكم) ثم ارمِ أول ورقة!"
        )
    }

    fun playBalootCard(card: BalootCard) {
        val current = _balootGameState.value
        val userBalance = currentUser.value.demoBalance

        if (userBalance < current.selectedStake && !current.isPlaying) {
            viewModelScope.launch {
                _toastMessage.emit("رصيدك التجريبي غير كافٍ لهذه الطاولة.")
            }
            return
        }

        val remaining = current.userCards.filter { it != card }
        val opponentCards = listOf(
            Pair("صقر (شريكك)", BalootCard("سبيت", "10", 10, "♠")),
            Pair("أبو راشد (الخصم)", BalootCard("سبيت", "7", 0, "♠")),
            Pair("سلطان (الخصم)", BalootCard("سبيت", "8", 0, "♠")),
            Pair("أنت", card)
        )

        val isTrickWon = card.value >= 10 || card.rankAr == "A" || card.rankAr == "J"
        val trickPoints = if (isTrickWon) 26 else 14
        val newOurScore = if (isTrickWon) current.ourScore + trickPoints else current.ourScore
        val newTheirScore = if (!isTrickWon) current.theirScore + trickPoints else current.theirScore

        val isFinalRound = remaining.isEmpty()
        val isGameWon = newOurScore >= newTheirScore

        _balootGameState.update {
            it.copy(
                isPlaying = true,
                userCards = remaining,
                tableCards = opponentCards,
                ourScore = newOurScore,
                theirScore = newTheirScore,
                currentRound = it.currentRound + 1,
                statusMessageAr = if (isTrickWon) "أكلت الأكلة! حصلتم على +$trickPoints بنط" else "أكل الخصم الأكلة +$trickPoints بنط",
                isGameFinished = isFinalRound,
                isWin = isGameWon,
                winAmount = if (isGameWon) (it.selectedStake * 2.0f).toLong() else 0L
            )
        }

        if (isFinalRound) {
            val winAmount = if (isGameWon) (current.selectedStake * 2.0f).toLong() else 0L
            repository.placeBetAndPlay(
                gameId = "game_baloot",
                gameTitleAr = "بلوت بيار الملكي",
                stake = current.selectedStake,
                isWin = isGameWon,
                multiplier = 2.0f,
                matchDetailsAr = "جولة ${current.gameTypeAr}: النتيجة $newOurScore - $newTheirScore",
                opponentName = "فريق أبو راشد وسلطان"
            )
            viewModelScope.launch {
                if (isGameWon) {
                    _toastMessage.emit("كفووو! فزتم بنشرة البلوت +$winAmount عملة تجريبية! 🏆")
                } else {
                    _toastMessage.emit("خسرت النشرة، حظ أوفر في القيد القادم!")
                }
            }
        }
    }

    fun restartBalootMatch() {
        initBalootGame()
    }

    fun setBalootMode(modeAr: String) {
        _balootGameState.update { it.copy(gameTypeAr = modeAr) }
    }
}
