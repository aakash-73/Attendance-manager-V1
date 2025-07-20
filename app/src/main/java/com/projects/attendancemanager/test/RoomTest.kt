import com.projects.attendancemanager.data.dao.SubjectDao
import com.projects.attendancemanager.db.model.Subject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun testRoomDatabaseConnection(subjectDao: SubjectDao): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            // Insert a test subject into the Room database
            val testSubject = Subject(name = "Test Subject")
            subjectDao.insertSubject(testSubject)

            // Query the subject to check if the insertion was successful
            val subjects = subjectDao.getAllSubjects().first() // Collect the first value

            if (subjects.isNotEmpty()) {
                true  // Successfully retrieved subjects
            } else {
                false  // No subjects found
            }
        } catch (e: Exception) {
            e.printStackTrace()  // Log the error
            false  // If an exception occurs, Room connection failed
        }
    }
}
