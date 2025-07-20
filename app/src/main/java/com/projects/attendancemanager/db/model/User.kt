@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.projects.attendancemanager.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bson.types.ObjectId

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: ObjectId = ObjectId.get(),  // Use ObjectId for MongoDB
    val username: String,
    val email: String,
    val password: String, // Store hashed password
    val role: String = "professor",// Default role set to "professor"
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
