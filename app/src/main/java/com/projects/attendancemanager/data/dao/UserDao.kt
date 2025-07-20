package com.projects.attendancemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import com.projects.attendancemanager.db.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    // Get users that are not synced to remote server
    @Query("SELECT * FROM users WHERE isSynced = 0")
    suspend fun getUnsyncedUsers(): List<User>

    // Get users that have been soft deleted locally but not yet deleted remotely
    @Query("SELECT * FROM users WHERE isDeleted = 1")
    suspend fun getDeletedUsers(): List<User>

    // Get users soft deleted AND synced (ready for hard delete)
    @Query("SELECT * FROM users WHERE isDeleted = 1 AND isSynced = 1")
    suspend fun getDeletedAndSyncedUsers(): List<User>

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    // Mark user as synced by setting isSynced = 1 using user id (ObjectId as string)
    @Query("UPDATE users SET isSynced = 1 WHERE id = :id")
    suspend fun markUserAsSynced(id: String)

    @Query("UPDATE users SET password = :newPassword WHERE email = :email")
    suspend fun updatePasswordByEmail(email: String, newPassword: String)

    @Query("SELECT * FROM users")
    suspend fun getAllUsersOnce(): List<User>


}