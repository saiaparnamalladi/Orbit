package com.orbit.app.presentation.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orbit.app.presentation.components.OrbitLogo
import com.orbit.app.presentation.components.StarBackground
import com.orbit.app.presentation.ui.theme.*
import com.orbit.app.utils.PhaseCalculator

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val phase = remember { PhaseCalculator.currentPhase() }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.success) {
        if (uiState.success) onRegisterSuccess()
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepSpace)) {
        StarBackground(phase = phase)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OrbitLogo(size = 80.dp)

            Spacer(Modifier.height(12.dp))

            Text(
                "create your orbit",
                style = MaterialTheme.typography.headlineMedium,
                color = StarWhite,
                textAlign = TextAlign.Center
            )
            Text(
                "just you and them",
                style = MaterialTheme.typography.bodyMedium,
                color = StarDim,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            OrbitTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "your name",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(Modifier.height(12.dp))

            OrbitTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "your email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(Modifier.height(12.dp))

            OrbitTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "your password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    viewModel.register(email, password, name)
                })
            )

            AnimatedVisibility(visible = uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            OrbitButton(
                text = if (uiState.isLoading) "creating…" else "create account",
                enabled = !uiState.isLoading,
                onClick = { viewModel.register(email, password, name) }
            )

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(
                    "already have an orbit? sign in",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RoseGold
                )
            }
        }
    }
}
