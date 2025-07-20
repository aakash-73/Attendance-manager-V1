package com.projects.attendancemanager.repository

import com.projects.attendancemanager.data.dao.StudentDao
import com.projects.attendancemanager.db.model.Student
import com.projects.attendancemanager.db.dbClient.MongoDBClient
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.bson.Document

class StudentRepository(
    private val studentDao: StudentDao
) {
    private val studentCollection = MongoDBClient.database.getCollection("students")

    suspend fun insertStudent(student: Student) {
        studentDao.insertStudent(student)
        syncStudentToMongo(student)
    }

    fun getStudentsForSubject(subjectId: Int): Flow<List<Student>> =
        studentDao.getStudentsBySubject(subjectId)

    fun getStudentsFromMongo(subjectId: Int): Flow<List<Student>> = flow {
        val documents = studentCollection.find(Filters.eq("subjectId", subjectId)).toList()
        val students = documents.mapNotNull { doc ->
            try {
                Student(
                    id = 0,
                    mongoId = doc.getString("mongoId") ?: "",
                    subjectId = doc.getInteger("subjectId"),
                    name = doc.getString("name") ?: ""
                )
            } catch (e: Exception) {
                null
            }
        }
        emit(students)
    }

    fun getAllStudents(): Flow<List<Student>> = studentDao.getAllStudents()

    suspend fun syncStudentToMongo(student: Student) {
        val doc = Document()
            .append("mongoId", student.mongoId)
            .append("subjectId", student.subjectId)
            .append("name", student.name)

        studentCollection.insertOne(doc)
    }

}
