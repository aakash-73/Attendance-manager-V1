package com.projects.attendancemanager.repository

import android.content.Context
import android.util.Log
import com.projects.attendancemanager.data.dao.UserDao
import com.projects.attendancemanager.db.model.User
import com.projects.attendancemanager.model.UserDto
import com.projects.attendancemanager.network.UserApiService
import com.projects.attendancemanager.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject
import org.bson.types.ObjectId
//import org.mindrot.jbcrypt.BCrypt
import com.password4j.Password
import com.password4j.Hash
import com.password4j.BcryptFunction
import at.favre.lib.crypto.bcrypt.BCrypt

class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao,
    private val userApiService: UserApiService
) {

    suspend fun registerUser(
        username: String,
        email: String,
        password: String,
        role: String = "professor"
    ): Result<Boolean> {
        return try {
            Log.d("UserRepository", "Registering user: $username")

            if (userDao.getUserByUsername(username) != null || userDao.getUserByEmail(email) != null) {
                Log.w("UserRepository", "Username or email already taken")
                return Result.failure(Exception("Username or email already taken"))
            }

            val hashedPassword = hashPassword(password)
            val isOnline = NetworkUtils.isNetworkAvailable(context)

            Log.d("UserRepository", "Network status: ${if (isOnline) "Online" else "Offline"}")

            val user = User(
                username = username,
                email = email,
                password = hashedPassword,
                role = role,
                isSynced = isOnline,
                isDeleted = false
            )

            userDao.insertUser(user)
            Log.d("UserRepository", "User inserted locally: $username")

            if (isOnline) {
                val userDto = UserDto(
                    _id = user.id.toHexString(), // Pass ObjectId string
                    username = user.username,
                    email = user.email,
                    password = user.password,
                    role = user.role
                )
                withContext(Dispatchers.IO) {
                    val response = userApiService.registerUserToDb(userDto)
                    Log.d("UserRepository", "Sync response: code=${response.code()}, body=${response.body()}, msg=${response.message()}")
                    if (!response.isSuccessful) throw Exception("Server error: ${response.message()}")
                }
            }

            Result.success(true)
        } catch (e: Exception) {
            Log.e("UserRepository", "Register error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteUserRemotely(userId: String): Result<Boolean> {
        return try {
            val response = userApiService.deleteUserFromDb(userId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Server error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncUnsyncedUsers() {
        val unsyncedUsers = userDao.getUnsyncedUsers()
        Log.d("UserRepository", "Syncing ${unsyncedUsers.size} unsynced users")

        for (user in unsyncedUsers) {
            try {
                val dto = UserDto(
                    _id = user.id.toHexString(),
                    username = user.username,
                    email = user.email,
                    password = user.password,
                    role = user.role
                )
                val response = userApiService.registerUserToDb(dto)
                if (response.isSuccessful) {
                    Log.d("UserRepository", "User synced successfully: ${user.username}")
                    userDao.markUserAsSynced(user.id.toHexString())
                } else {
                    Log.w("UserRepository", "Failed to sync user ${user.username}: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Sync error for ${user.username}: ${e.message}", e)
            }
        }
    }

    // Updated syncDeletedUsers to only sync users with isDeleted=1 AND isSynced=1
    suspend fun syncDeletedUsers() {
        val deletedAndSyncedUsers = userDao.getDeletedAndSyncedUsers()
        Log.d("UserRepository", "Syncing ${deletedAndSyncedUsers.size} deleted and synced users")

        for (user in deletedAndSyncedUsers) {
            try {
                val response = userApiService.deleteUserFromDb(user.id.toHexString())
                if (response.isSuccessful) {
                    userDao.deleteUser(user)  // hard delete locally after remote success
                    Log.d("UserRepository", "Deleted user synced successfully: ${user.username}")
                } else {
                    Log.w("UserRepository", "Failed to sync delete for user ${user.username}: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Sync error deleting user ${user.username}: ${e.message}", e)
            }
        }
    }

    // Mark user deleted and mark isSynced=true to trigger hard delete on next sync
    suspend fun markUserDeleted(username: String): Result<Boolean> {
        return try {
            val user = userDao.getUserByUsername(username) ?: return Result.failure(Exception("User not found"))
            val deletedUser = user.copy(isDeleted = true, isSynced = true)
            userDao.updateUser(deletedUser)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(username: String): Result<Boolean> {
        return markUserDeleted(username)
    }

    suspend fun loginUser(identifier: String, password: String): Result<User> {
        return try {
            Log.d("UserRepository", "Attempting login for: $identifier with password: $password")

            var user = userDao.getUserByUsername(identifier)
            if (user == null) {
                user = userDao.getUserByEmail(identifier)
            }

            if (user == null) {
                Log.w("UserRepository", "User not found for identifier: $identifier")
                return Result.failure(Exception("User not found"))
            }

            Log.d("UserRepository", "Verifying input password: '$password' with stored hash: '${user.password}'")
            if (!verifyPassword(password, user.password)) {

                Log.w("UserRepository", "Password verification failed for $identifier")
                return Result.failure(Exception("Incorrect password"))
            }

            Log.d("UserRepository", "Password verification succeeded for $identifier")

            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Login error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Boolean {
        val user = userDao.getUserByEmail(email)
        return user != null
    }

    /**
     * Updates the password for a user in the local database
     * This method is used when a password is reset via the reset password flow
     */
    suspend fun updateUserPassword(email: String, hashedPasswordFromServer: String): Result<Boolean> {
        return try {
            Log.d("UserRepository", "Updating password for user: $email")

            val user = userDao.getUserByEmail(email)
            if (user == null) {
                Log.w("UserRepository", "User not found with email: $email")
                return Result.failure(Exception("User not found"))
            }

            val updatedUser = user.copy(
                password = hashedPasswordFromServer,
                isSynced = true
            )

            userDao.updateUser(updatedUser)
            Log.d("UserRepository", "Password updated successfully for user: $email")

            // Print the updated hash from DB to verify
            val userAfterUpdate = userDao.getUserByEmail(email)
            Log.d("UserRepository", "After update, stored hash = ${userAfterUpdate?.password}")

            Result.success(true)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating password for $email: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun getUsers(): Flow<List<User>> = userDao.getAllUsers()

    private fun hashPassword(password: String): String {
        val hashed = BCrypt.withDefaults().hashToString(10, password.toCharArray())
        Log.d("UserRepository", "Generated hash: $hashed")
        return hashed
    }

    private fun verifyPassword(inputPassword: String, storedPassword: String): Boolean {
        return try {
            val result = BCrypt.verifyer().verify(inputPassword.toCharArray(), storedPassword)
            Log.d("UserRepository", "Password verification result: ${result.verified}")
            result.verified
        } catch (e: Exception) {
            Log.e("UserRepository", "BCrypt verification error: ${e.message}", e)
            false
        }
    }

    suspend fun logAllUsersInDb() {
        try {
            val users = userDao.getAllUsersOnce()
            Log.d("UserRepository", "=== Dumping Users from Room DB ===")
            for (user in users) {
                Log.d("UserRepository", "User -> id=${user.id}, username=${user.username}, email=${user.email}, hash=${user.password}")
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error dumping users: ${e.message}", e)
        }
    }

}

