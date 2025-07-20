@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.projects.attendancemanager.db.model

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@SuppressLint("UnsafeOptInUsageError")
@Entity(tableName = "timetable")
@Serializable
data class Timetable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Room ID
    val mongoId: String = ObjectId().toHexString(), // MongoDB ID
    val subjectId: Int, // Foreign key to Subject
    val days: List<String>, // ["Monday", "Wednesday"]
    val startTime: String, // "09:00 AM"
    val endTime: String // "10:30 AM"
)