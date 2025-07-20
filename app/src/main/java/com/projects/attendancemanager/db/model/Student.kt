@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.projects.attendancemanager.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Entity(tableName = "students")
@Serializable
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Room ID
    val mongoId: String = ObjectId().toHexString(), // MongoDB ID
    val subjectId: Int, // Foreign key reference to Subject
    val name: String
)
