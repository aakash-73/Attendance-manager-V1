package com.projects.attendancemanager.repository

import com.projects.attendancemanager.data.dao.AttendanceDao
import com.projects.attendancemanager.db.dbClient.MongoDBClient
import com.projects.attendancemanager.db.model.Attendance
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.bson.Document

class AttendanceRepository(
    private val attendanceDao: AttendanceDao
) {
    private val attendanceCollection: MongoCollection<Document> =
        MongoDBClient.database.getCollection("attendance")

    suspend fun markAttendance(attendance: Attendance) {
        attendanceDao.markAttendance(attendance)
        syncAttendanceToMongo(attendance)
    }

    fun getAttendanceForStudent(studentId: Int): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForStudent(studentId)

    fun getAttendanceHistoryFromMongo(studentId: Int): Flow<List<Attendance>> = flow {
        val documents = attendanceCollection.find(Filters.eq("studentId", studentId))
        val history = documents.map {
            Attendance(
                mongoId = it.getString("mongoId") ?: "",
                studentId = it.getInteger("studentId"),
                date = it.getString("date") ?: "",
                isPresent = it.getBoolean("isPresent", false)
            )
        }.toList()
        emit(history)
    }

    fun getAllAttendance(): Flow<List<Attendance>> = attendanceDao.getAllAttendance()

    suspend fun syncAttendanceToMongo(attendance: Attendance) {
        val document = Document().apply {
            put("mongoId", attendance.mongoId)
            put("studentId", attendance.studentId)
            put("date", attendance.date)
            put("isPresent", attendance.isPresent)
        }

        attendanceCollection.insertOne(document)
    }

}
