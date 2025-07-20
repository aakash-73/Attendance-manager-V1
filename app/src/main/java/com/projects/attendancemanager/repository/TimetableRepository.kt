package com.projects.attendancemanager.repository

import com.projects.attendancemanager.data.dao.TimetableDao
import com.projects.attendancemanager.db.dbClient.MongoDBClient
import com.projects.attendancemanager.db.model.Timetable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import org.bson.Document

class TimetableRepository(
    private val timetableDao: TimetableDao
) {
    private val timetableCollection = MongoDBClient.database.getCollection("timetables")

    suspend fun insertTimetable(timetable: Timetable) {
        timetableDao.insertTimetable(timetable)
        syncTimetableToMongo(timetable)
    }

    fun getTimetableForSubject(subjectId: Int): Flow<Timetable?> {
        return timetableDao.getTimetableForSubject(subjectId)
    }

    fun getTimetableHistoryFromMongo(): Flow<List<Timetable>> = flow {
        val documents = timetableCollection.find().toList()
        val timetables = documents.mapNotNull { doc ->
            try {
                Timetable(
                    id = 0,
                    mongoId = doc.getString("mongoId") ?: "",
                    subjectId = doc.getInteger("subjectId") ?: -1,
                    days = doc.getList("days", String::class.java) ?: emptyList(),
                    startTime = doc.getString("startTime") ?: "",
                    endTime = doc.getString("endTime") ?: ""
                )
            } catch (e: Exception) {
                null
            }
        }
        emit(timetables)
    }

    fun getAllTimetables(): Flow<List<Timetable>> = timetableDao.getAllTimetables()

    suspend fun syncTimetableToMongo(timetable: Timetable) {
        val doc = Document()
            .append("mongoId", timetable.mongoId)
            .append("subjectId", timetable.subjectId)
            .append("days", timetable.days)
            .append("startTime", timetable.startTime)
            .append("endTime", timetable.endTime)

        timetableCollection.insertOne(doc)
    }

}
