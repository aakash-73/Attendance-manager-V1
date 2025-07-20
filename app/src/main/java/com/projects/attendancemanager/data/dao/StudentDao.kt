package com.projects.attendancemanager.data.dao

import androidx.room.*
import com.projects.attendancemanager.db.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Query("SELECT * FROM students WHERE subjectId = :subjectId")
    fun getStudentsBySubject(subjectId: Int): Flow<List<Student>>

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<Student>>

}
