package com.projects.attendancemanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.projects.attendancemanager.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordResult>(ForgotPasswordResult.Idle)
    val forgotPasswordState: StateFlow<ForgotPasswordResult> = _forgotPasswordState

    fun resetPassword(email: String) {
        _forgotPasswordState.value = ForgotPasswordResult.Loading

        viewModelScope.launch {
            try {
                val result = userRepository.sendPasswordResetEmail(email) // Check if the email exists in DB
                _forgotPasswordState.value = if (result) {
                    ForgotPasswordResult.Success
                } else {
                    ForgotPasswordResult.Error("Email not found")
                }
            } catch (e: Exception) {
                _forgotPasswordState.value = ForgotPasswordResult.Error("Error: ${e.localizedMessage}")
            }
        }
    }
}

sealed class ForgotPasswordResult {
    object Idle : ForgotPasswordResult()
    object Loading : ForgotPasswordResult()
    object Success : ForgotPasswordResult()
    data class Error(val message: String) : ForgotPasswordResult()
}
