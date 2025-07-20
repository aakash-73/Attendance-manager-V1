package com.projects.attendancemanager.data.dao

import androidx.room.*
import com.projects.attendancemanager.db.model.Attendance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    fun getAttendanceForStudent(studentId: Int): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance")
    fun getAllAttendance(): Flow<List<Attendance>>

}
