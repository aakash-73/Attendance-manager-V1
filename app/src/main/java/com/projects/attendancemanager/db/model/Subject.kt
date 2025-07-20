@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.projects.attendancemanager.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Entity(tableName = "subjects")
@Serializable
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Room uses this
    val mongoId: String = ObjectId().toHexString(), // MongoDB uses this
    val name: String
)
