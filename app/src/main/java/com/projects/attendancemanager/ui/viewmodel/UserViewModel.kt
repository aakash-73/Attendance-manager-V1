package com.projects.attendancemanager.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.attendancemanager.db.model.User
import com.projects.attendancemanager.model.UserDto
import com.projects.attendancemanager.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<UserState>(UserState.Idle)
    val userState: StateFlow<UserState> = _userState

    private var hasJustRegistered = false

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            try {
                val user = userRepository.getUsers().first().firstOrNull()
                if (user != null) {
                    if (hasJustRegistered) {
                        _userState.value = UserState.Success(user)
                        hasJustRegistered = false
                    } else {
                        _userState.value = UserState.Idle
                    }
                } else {
                    _userState.value = UserState.Idle
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Failed to fetch user data")
            }
        }
    }

    fun registerUser(
        username: String,
        email: String,
        password: String,
        role: String = "professor"
    ) {
        _userState.value = UserState.Loading
        viewModelScope.launch {
            val result = userRepository.registerUser(username, email, password, role)
            result
                .onSuccess {
                    hasJustRegistered = true
                    fetchUserData()
                }
                .onFailure {
                    _userState.value = UserState.Error(it.message ?: "Registration failed")
                }
        }
    }
}

/**
 * Represents the current state of the user registration or data fetch.
 */
sealed class UserState {
    object Idle : UserState()
    object Loading : UserState()
    data class Error(val message: String) : UserState()
    data class Success(val user: User) : UserState()
}
