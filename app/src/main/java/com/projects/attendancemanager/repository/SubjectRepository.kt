package com.projects.attendancemanager.repository

import com.projects.attendancemanager.data.dao.SubjectDao
import com.projects.attendancemanager.db.dbClient.MongoDBClient
import com.projects.attendancemanager.db.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import org.bson.Document

class SubjectRepository(
    private val subjectDao: SubjectDao
) {
    private val subjectCollection = MongoDBClient.database.getCollection("subjects")

    suspend fun insertSubject(subject: Subject) {
        subjectDao.insertSubject(subject)
        syncSubjectToMongo(subject)
    }

    fun getSubjects(): Flow<List<Subject>> = subjectDao.getAllSubjects()

    suspend fun getAllSubjects(): List<Subject> = subjectDao.getAllSubjectsOnce()

    suspend fun syncSubjectToMongo(subject: Subject) {
        val doc = Document()
            .append("mongoId", subject.mongoId)
            .append("name", subject.name)

        subjectCollection.insertOne(doc)
    }


    fun getSubjectsFromMongo(): Flow<List<Subject>> = flow {
        val documents = subjectCollection.find().toList()
        val subjects = documents.mapNotNull { doc ->
            try {
                Subject(
                    id = 0,
                    mongoId = doc.getString("mongoId") ?: "",
                    name = doc.getString("name") ?: ""
                )
            } catch (e: Exception) {
                null
            }
        }
        emit(subjects)
    }
}
