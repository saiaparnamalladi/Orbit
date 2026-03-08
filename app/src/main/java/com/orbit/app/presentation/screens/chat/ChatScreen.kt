package com.orbit.app.presentation.screens.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orbit.app.presentation.components.HeartButton
import com.orbit.app.presentation.components.IncomingHeartOverlay
import com.orbit.app.presentation.components.StarBackground
import com.orbit.app.presentation.ui.theme.*
import com.orbit.app.utils.PhaseCalculator
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    val phase = remember { PhaseCalculator.currentPhase() }

    var inputText by remember { mutableStateOf("") }
    var showSlashMenu by remember { mutableStateOf(false) }

    // Auto-scroll on new messages
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepSpace)) {
        StarBackground(phase = phase)

        // Gradient overlay to help readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(DeepSpace.copy(alpha = 0.3f), DeepSpace.copy(alpha = 0.7f))
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top bar ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        uiState.partnerName.ifEmpty { "…" },
                        style = MaterialTheme.typography.headlineMedium,
                        color = StarWhite
                    )
                    Text(
                        "in your orbit",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoseGold
                    )
                }

                HeartButton(size = 48.dp, onClick = { viewModel.sendHeart() })
            }

            HorizontalDivider(color = CosmicBlue, thickness = 1.dp)

            // ── Messages ──────────────────────────────────────
            LazyColumn(
                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                state = listState,
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        isOwn = message.senderId == viewModel.myUid
                    )
                }
            }

            // ── Slash command suggestions ──────────────────────
            AnimatedVisibility(
                visible = showSlashMenu,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                SlashCommandMenu { command ->
                    inputText = command
                    showSlashMenu = false
                }
            }

            // ── Input bar ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Midnight.copy(alpha = 0.9f))
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { text ->
                        inputText = text
                        showSlashMenu = text == "/"
                    },
                    placeholder = {
                        Text("say something…", color = StarDim, style = MaterialTheme.typography.bodyMedium)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoseGold.copy(alpha = 0.5f),
                        unfocusedBorderColor = CosmicBlue,
                        focusedTextColor = StarWhite,
                        unfocusedTextColor = StarWhite,
                        cursorColor = RoseGold,
                        focusedContainerColor = CosmicBlue.copy(alpha = 0.4f),
                        unfocusedContainerColor = CosmicBlue.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText.trim())
                            inputText = ""
                            showSlashMenu = false
                        }
                    }),
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText.trim())
                            inputText = ""
                            showSlashMenu = false
                            keyboard?.hide()
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(RoseGold, CircleShape)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = DeepSpace)
                }
            }
        }

        // ── Incoming heart overlay ────────────────────────────
        AnimatedVisibility(
            visible = uiState.incomingHeart,
            modifier = Modifier.align(Alignment.Center),
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            IncomingHeartOverlay(onDone = { viewModel.dismissIncomingHeart() })
        }
    }
}

@Composable
fun SlashCommandMenu(onSelect: (String) -> Unit) {
    val commands = listOf(
        "/miss" to "💛 missing you",
        "/soon" to "⏳ see you soon",
        "/goodnight" to "🌙 goodnight",
        "/morning" to "🌅 good morning",
        "/hug" to "🫂 sending a hug"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Midnight.copy(alpha = 0.95f))
            .padding(vertical = 4.dp)
    ) {
        commands.forEach { (cmd, label) ->
            TextButton(
                onClick = { onSelect("$cmd ") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(label, color = StarWhite, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(cmd, color = RoseGold.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
