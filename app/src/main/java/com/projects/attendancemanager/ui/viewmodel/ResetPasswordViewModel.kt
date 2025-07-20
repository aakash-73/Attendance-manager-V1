package com.projects.attendancemanager.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.attendancemanager.data.dao.UserDao
import com.projects.attendancemanager.network.ResetPasswordRequest
import com.projects.attendancemanager.network.ResetPasswordSubmitRequest
import com.projects.attendancemanager.network.ResetPasswordResponse // ✅ NEW: Response model
import com.projects.attendancemanager.network.RetrofitClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _resetPasswordState = MutableStateFlow<ResetPasswordState>(ResetPasswordState.Idle)
    val resetPasswordState: StateFlow<ResetPasswordState> = _resetPasswordState

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _resetPasswordState.value = ResetPasswordState.Error("Email cannot be empty")
            return
        }

        if (!email.contains("@")) {
            _resetPasswordState.value = ResetPasswordState.Error("Invalid email format")
            return
        }

        viewModelScope.launch {
            _resetPasswordState.value = ResetPasswordState.Loading

            try {
                val response = RetrofitClient.api.resetPassword(
                    ResetPasswordRequest(email)
                )

                if (response.isSuccessful) {
                    _resetPasswordState.value = ResetPasswordState.Success
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _resetPasswordState.value = ResetPasswordState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _resetPasswordState.value = ResetPasswordState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }

    fun submitNewPassword(email: String, newPassword: String) {
        if (email.isBlank() || newPassword.isBlank()) {
            _resetPasswordState.value = ResetPasswordState.Error("Email and new password cannot be empty")
            return
        }

        if (!email.contains("@")) {
            _resetPasswordState.value = ResetPasswordState.Error("Invalid email format")
            return
        }

        viewModelScope.launch {
            _resetPasswordState.value = ResetPasswordState.Loading

            try {
                val response = RetrofitClient.api.submitNewPassword(
                    ResetPasswordSubmitRequest(email, newPassword)
                )

                if (response.isSuccessful) {
                    // ✅ Extract hashed password from typed response
                    val hashedPassword = (response.body() as? ResetPasswordResponse)?.hashedPassword

                    if (!hashedPassword.isNullOrBlank()) {
                        userDao.updatePasswordByEmail(email, hashedPassword)

                        val updatedUser = userDao.getUserByEmail(email)
                        Log.d("PasswordUpdate", "Local DB hashed password = ${updatedUser?.password}")

                        _resetPasswordState.value = ResetPasswordState.Success
                    } else {
                        _resetPasswordState.value = ResetPasswordState.Error("Missing hashed password in response")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _resetPasswordState.value = ResetPasswordState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _resetPasswordState.value = ResetPasswordState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }
}

sealed class ResetPasswordState {
    object Idle : ResetPasswordState()
    object Loading : ResetPasswordState()
    object Success : ResetPasswordState()
    data class Error(val message: String) : ResetPasswordState()
}
