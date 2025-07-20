@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.projects.attendancemanager.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Entity(tableName = "attendance")
@Serializable
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Room ID
    val mongoId: String = ObjectId().toHexString(), // MongoDB ID
    val studentId: Int, // Foreign key to Student
    val date: String, // Store as YYYY-MM-DD
    val isPresent: Boolean
)
