package com.projects.attendancemanager.data.dao

import androidx.room.*
import com.projects.attendancemanager.db.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<Subject>>

    @Delete
    suspend fun deleteSubject(subject: Subject)

    @Query("SELECT * FROM subjects")
    suspend fun getAllSubjectsOnce(): List<Subject>
}
