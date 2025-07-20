package com.projects.attendancemanager.data.dao

import androidx.room.*
import com.projects.attendancemanager.db.model.Timetable
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetable(timetable: Timetable)

    @Query("SELECT * FROM timetable WHERE subjectId = :subjectId")
    fun getTimetableForSubject(subjectId: Int): Flow<Timetable?>

    @Delete
    suspend fun deleteTimetable(timetable: Timetable)

    @Query("SELECT * FROM timetable")
    fun getAllTimetables(): Flow<List<Timetable>>

}
