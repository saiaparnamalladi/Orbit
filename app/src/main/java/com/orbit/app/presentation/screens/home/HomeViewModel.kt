package com.orbit.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.app.data.repository.AuthRepository
import com.orbit.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPaired: Boolean = false,
    val myPairCode: String = "",
    val partnerName: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val uid = authRepo.currentUser?.uid ?: return@launch
            val user = userRepo.getUser(uid) ?: return@launch
            _uiState.value = _uiState.value.copy(
                myPairCode = user.pairCode,
                isPaired = user.partnerId.isNotEmpty()
            )
            if (user.partnerId.isNotEmpty()) {
                val partner = userRepo.getUser(user.partnerId)
                _uiState.value = _uiState.value.copy(partnerName = partner?.name ?: "")
            }
        }
    }

    fun pairWithPartner(code: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val myUid = authRepo.currentUser?.uid ?: return@launch
            val myUser = userRepo.getUser(myUid)

            if (code == myUser?.pairCode) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "that's your own code 💛"
                )
                return@launch
            }

            val partner = userRepo.getUserByPairCode(code)
            if (partner == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "code not found — double check?"
                )
                return@launch
            }

            userRepo.pairUsers(myUid, partner.uid)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isPaired = true,
                partnerName = partner.name
            )
        }
    }
}
