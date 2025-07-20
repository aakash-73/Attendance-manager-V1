package com.projects.attendancemanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.attendancemanager.db.model.User
import com.projects.attendancemanager.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(emailOrUsername: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            userRepository.logAllUsersInDb()
            val result = userRepository.loginUser(emailOrUsername, password)
            _loginState.value = result.fold(
                onSuccess = { LoginState.Success(it) },
                onFailure = { LoginState.Error(it.localizedMessage ?: "Login failed") }
            )
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
        // Removed _username and _password resets
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
