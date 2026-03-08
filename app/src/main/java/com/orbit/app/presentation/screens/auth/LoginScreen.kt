package com.orbit.app.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
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
import com.orbit.app.utils.DayPhase
import com.orbit.app.utils.PhaseCalculator

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val phase = remember { PhaseCalculator.currentPhase() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.success) {
        if (uiState.success) onLoginSuccess()
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
            OrbitLogo(size = 100.dp)

            Spacer(Modifier.height(16.dp))

            Text(
                text = "orbit",
                style = MaterialTheme.typography.displayMedium,
                color = StarWhite,
                textAlign = TextAlign.Center
            )

            Text(
                text = PhaseCalculator.phaseLabel(phase),
                style = MaterialTheme.typography.bodyMedium,
                color = StarDim,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(48.dp))

            OrbitTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "your email",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(Modifier.height(12.dp))

            OrbitTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "your password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.login(email, password)
                    }
                )
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
                text = if (uiState.isLoading) "signing in…" else "sign in",
                enabled = !uiState.isLoading,
                onClick = { viewModel.login(email, password) }
            )

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(
                    "new here? create your orbit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RoseGold
                )
            }
        }
    }
}

// ── Shared UI Components ──────────────────────────────────

@Composable
fun OrbitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = StarDim, style = MaterialTheme.typography.bodyMedium)
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RoseGold,
            unfocusedBorderColor = CosmicBlue,
            focusedTextColor = StarWhite,
            unfocusedTextColor = StarWhite,
            cursorColor = RoseGold,
            focusedContainerColor = Midnight.copy(alpha = 0.6f),
            unfocusedContainerColor = Midnight.copy(alpha = 0.4f)
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun OrbitButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = RoseGold,
            contentColor = DeepSpace,
            disabledContainerColor = RoseGoldDark.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}
