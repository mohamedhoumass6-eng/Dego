package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BiaarBottomNav
import com.example.ui.components.BiaarTopBar
import com.example.ui.components.CreateRoomDialog
import com.example.ui.components.GameDetailSheet
import com.example.ui.components.RechargeDemoSheet
import com.example.ui.theme.BiaarDarkBg
import com.example.ui.viewmodel.BiaarNavTab
import com.example.ui.viewmodel.BiaarViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainScreen(
    viewModel: BiaarViewModel = viewModel()
) {
    val context = LocalContext.current
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val games by viewModel.games.collectAsState()

    val showRechargeSheet by viewModel.showRechargeSheet.collectAsState()
    val showCreateRoomDialog by viewModel.showCreateRoomDialog.collectAsState()
    val selectedGameForDetails by viewModel.selectedGameForDetails.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Collect Toast notifications
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Force Arabic RTL Layout Direction for the entire application
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BiaarDarkBg)
        ) {
            if (!isAuthenticated) {
                AuthScreen(viewModel = viewModel)
            } else {
                Scaffold(
                    topBar = {
                        BiaarTopBar(
                            user = currentUser,
                            onWalletClick = { viewModel.selectTab(BiaarNavTab.WALLET) },
                            onProfileClick = { viewModel.selectTab(BiaarNavTab.PROFILE) },
                            onRechargeClick = { viewModel.setRechargeSheetVisible(true) }
                        )
                    },
                    bottomBar = {
                        BiaarBottomNav(
                            currentTab = currentTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = BiaarDarkBg
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "screen_transition"
                        ) { tab ->
                            when (tab) {
                                BiaarNavTab.HOME -> HomeScreen(viewModel = viewModel)
                                BiaarNavTab.PLAY_ROOM -> PlayRoomScreen(viewModel = viewModel)
                                BiaarNavTab.BETS_HISTORY -> BetsHistoryScreen(viewModel = viewModel)
                                BiaarNavTab.WALLET -> WalletScreen(viewModel = viewModel)
                                BiaarNavTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                            }
                        }
                    }
                }

                // Modal Sheets & Dialogs
                if (showRechargeSheet) {
                    RechargeDemoSheet(
                        onDismiss = { viewModel.setRechargeSheetVisible(false) },
                        onRecharge = { amount -> viewModel.rechargeDemoBalance(amount) }
                    )
                }

                if (showCreateRoomDialog) {
                    CreateRoomDialog(
                        games = games,
                        onDismiss = { viewModel.setCreateRoomDialogVisible(false) },
                        onCreate = { gameId, titleAr, roomName, stake ->
                            viewModel.createRoom(gameId, titleAr, roomName, stake)
                        }
                    )
                }

                selectedGameForDetails?.let { game ->
                    GameDetailSheet(
                        game = game,
                        onDismiss = { viewModel.selectGameForDetails(null) },
                        onPlayNow = { targetGame ->
                            viewModel.openPlayRoomForGame(targetGame)
                        },
                        onCreateRoom = {
                            viewModel.selectGameForDetails(null)
                            viewModel.setCreateRoomDialogVisible(true)
                        }
                    )
                }
            }
        }
    }
}
