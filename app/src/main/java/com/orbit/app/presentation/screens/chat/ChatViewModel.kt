package com.orbit.app.presentation.screens.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.app.data.model.HeartPulse
import com.orbit.app.data.model.Message
import com.orbit.app.data.repository.AuthRepository
import com.orbit.app.data.repository.ChatRepository
import com.orbit.app.data.repository.HeartRepository
import com.orbit.app.data.repository.UserRepository
import com.orbit.app.utils.HapticUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val partnerName: String = "",
    val chatId: String = "",
    val incomingHeart: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val chatRepo: ChatRepository,
    private val heartRepo: HeartRepository,
    private val userRepo: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    val myUid: String get() = authRepo.currentUser?.uid ?: ""

    init {
        initChat()
    }

    private fun initChat() {
        viewModelScope.launch {
            val uid = authRepo.currentUser?.uid ?: return@launch
            val user = userRepo.getUser(uid) ?: return@launch
            val partnerId = user.partnerId.ifEmpty { return@launch }
            val partner = userRepo.getUser(partnerId)
            val chatId = chatRepo.chatId(uid, partnerId)

            _uiState.value = _uiState.value.copy(
                chatId = chatId,
                partnerName = partner?.name ?: "them",
                isLoading = false
            )

            // Observe messages
            chatRepo.observeMessages(chatId)
                .collect { msgs ->
                    _uiState.value = _uiState.value.copy(messages = msgs)
                }
        }

        // Observe incoming heart pulses
        viewModelScope.launch {
            val uid = authRepo.currentUser?.uid ?: return@launch
            val user = userRepo.getUser(uid) ?: return@launch
            val partnerId = user.partnerId.ifEmpty { return@launch }
            val chatId = chatRepo.chatId(uid, partnerId)

            heartRepo.observePulses(chatId).collect { pulse ->
                if (pulse != null && pulse.senderId != uid) {
                    HapticUtil.receivedHeart(context)
                    _uiState.value = _uiState.value.copy(incomingHeart = true)
                }
            }
        }
    }

    fun sendMessage(text: String) {
        val chatId = _uiState.value.chatId.ifEmpty { return }
        viewModelScope.launch {
            HapticUtil.messageSent(context)
            chatRepo.sendMessage(chatId, myUid, text)
        }
    }

    fun sendHeart() {
        val chatId = _uiState.value.chatId.ifEmpty { return }
        viewModelScope.launch {
            HapticUtil.heartPulse(context)
            heartRepo.sendPulse(chatId)
            chatRepo.sendHeartMessage(chatId, myUid)
        }
    }

    fun dismissIncomingHeart() {
        _uiState.value = _uiState.value.copy(incomingHeart = false)
    }
}
