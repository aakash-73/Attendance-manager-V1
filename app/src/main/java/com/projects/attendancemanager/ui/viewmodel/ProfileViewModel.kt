package com.projects.attendancemanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.attendancemanager.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.projects.attendancemanager.data.dao.UserDao
import com.projects.attendancemanager.db.model.User

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userDao: UserDao
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        viewModelScope.launch {
            // Fetch user data (replace this with actual user retrieval logic)
            _user.value = userDao.getUserByUsername("test_user") // Example user fetch logic
        }
    }

    fun logout() {
        // Handle logout logic (clear session, navigate to login screen, etc.)
    }
}
