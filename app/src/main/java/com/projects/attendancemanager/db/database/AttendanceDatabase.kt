package com.projects.attendancemanager.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.projects.attendancemanager.data.dao.*
import com.projects.attendancemanager.db.model.*
import com.projects.attendancemanager.db.Converters // Import the Converters class

@Database(
    entities = [Subject::class, Student::class, Attendance::class, Timetable::class, User::class],
    version = 5, // Updated version if migration is needed
    exportSchema = true // Set to true for tracking schema changes
)
@TypeConverters(Converters::class) // Register Converters to handle List<String>
abstract class AttendanceDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun timetableDao(): TimetableDao // Added Timetable
    abstract fun userDao(): UserDao
}
