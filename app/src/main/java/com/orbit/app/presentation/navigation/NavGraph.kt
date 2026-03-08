package com.orbit.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.orbit.app.data.repository.AuthRepository
import com.orbit.app.data.repository.UserRepository
import com.orbit.app.presentation.components.OrbitLogo
import com.orbit.app.presentation.components.StarBackground
import com.orbit.app.presentation.screens.auth.LoginScreen
import com.orbit.app.presentation.screens.auth.RegisterScreen
import com.orbit.app.presentation.screens.chat.ChatScreen
import com.orbit.app.presentation.screens.home.PairScreen
import com.orbit.app.utils.PhaseCalculator
import kotlinx.coroutines.launch

object Routes {
    const val LOGIN    = "login"
    const val REGISTER = "register"
    const val PAIR     = "pair"
    const val CHAT     = "chat"
}

@Composable
fun OrbitNavGraph(
    authRepo: AuthRepository,
    userRepo: UserRepository
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // Determine start destination
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val user = authRepo.currentUser
            startDestination = when {
                user == null -> Routes.LOGIN
                else -> {
                    val userData = runCatching { userRepo.getUser(user.uid) }.getOrNull()
                    if (userData?.partnerId.isNullOrEmpty()) Routes.PAIR else Routes.CHAT
                }
            }
        } catch (_: Exception) {
            // If anything goes wrong while resolving the start destination,
            // fall back to the login screen instead of leaving the UI blank.
            startDestination = Routes.LOGIN
        }
    }

    if (startDestination == null) {
        val phase = remember { PhaseCalculator.currentPhase() }
        Box(modifier = Modifier.fillMaxSize()) {
            StarBackground(phase = phase)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                OrbitLogo()
            }
        }
        return
    }

    NavHost(navController = navController, startDestination = startDestination!!) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    scope.launch {
                        val uid = authRepo.currentUser?.uid ?: return@launch
                        val userData = userRepo.getUser(uid)
                        val dest = if (userData?.partnerId.isNullOrEmpty()) Routes.PAIR else Routes.CHAT
                        navController.navigate(dest) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.PAIR) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.PAIR) {
            PairScreen(
                onPaired = {
                    navController.navigate(Routes.CHAT) {
                        popUpTo(Routes.PAIR) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CHAT) {
            ChatScreen()
        }
    }
}
