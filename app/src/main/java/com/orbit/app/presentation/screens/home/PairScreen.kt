package com.orbit.app.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orbit.app.presentation.components.OrbitLogo
import com.orbit.app.presentation.components.StarBackground
import com.orbit.app.presentation.screens.auth.OrbitButton
import com.orbit.app.presentation.screens.auth.OrbitTextField
import com.orbit.app.presentation.ui.theme.*
import com.orbit.app.utils.PhaseCalculator
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.sp
import com.orbit.app.presentation.screens.home.HomeViewModel


@Composable
fun PairScreen(
    onPaired: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val phase = remember { PhaseCalculator.currentPhase() }

    var partnerCode by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isPaired) {
        if (uiState.isPaired) onPaired()
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
            OrbitLogo(size = 90.dp)

            Spacer(Modifier.height(24.dp))

            Text(
                "find your orbit",
                style = MaterialTheme.typography.headlineMedium,
                color = StarWhite,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "share your code with your partner,\nor enter theirs below",
                style = MaterialTheme.typography.bodyMedium,
                color = StarDim,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // Your code display
            Text("your code", style = MaterialTheme.typography.labelSmall, color = StarDim)
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(CosmicBlue.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .border(1.dp, RoseGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(
                    text = uiState.myPairCode.ifEmpty { "loading…" },
                    style = MaterialTheme.typography.displayMedium.copy(
                        letterSpacing = 8.sp,
                        fontWeight = FontWeight.Light
                    ),
                    color = RoseGold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(40.dp))

            HorizontalDivider(color = CosmicBlue, thickness = 1.dp)

            Spacer(Modifier.height(32.dp))

            Text("partner's code", style = MaterialTheme.typography.labelSmall, color = StarDim)
            Spacer(Modifier.height(8.dp))

            OrbitTextField(
                value = partnerCode,
                onValueChange = { if (it.length <= 6) partnerCode = it.uppercase() },
                placeholder = "enter their code",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters
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

            Spacer(Modifier.height(20.dp))

            OrbitButton(
                text = if (uiState.isLoading) "connecting…" else "connect",
                enabled = partnerCode.length == 6 && !uiState.isLoading,
                onClick = { viewModel.pairWithPartner(partnerCode) }
            )
        }
    }
}
