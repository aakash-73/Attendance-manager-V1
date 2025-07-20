package com.projects.attendancemanager.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.projects.attendancemanager.repository.AttendanceRepository
import com.projects.attendancemanager.repository.SubjectRepository
import com.projects.attendancemanager.repository.StudentRepository
import com.projects.attendancemanager.repository.TimetableRepository
import com.projects.attendancemanager.repository.UserRepository
import com.projects.attendancemanager.utils.NetworkUtils
import com.projects.attendancemanager.worker.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@ServiceScoped
class SyncService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val attendanceRepo: AttendanceRepository,
    private val subjectRepo: SubjectRepository,
    private val studentRepo: StudentRepository,
    private val timetableRepo: TimetableRepository,
    private val userRepo: UserRepository
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NetworkReceiver", "Network available: ${NetworkUtils.isNetworkAvailable(context)}")
        if (NetworkUtils.isNetworkAvailable(context)) {
            Log.d("NetworkReceiver", "Enqueuing SyncWorker")
            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
            WorkManager.getInstance(context).enqueue(syncRequest)
        }
    }



    private suspend fun syncDataToMongoDB() {
        // Sync Subjects to MongoDB
        subjectRepo.getSubjects().first().forEach { subject ->
            subjectRepo.syncSubjectToMongo(subject)
        }

        // Sync Students to MongoDB
        studentRepo.getAllStudents().first().forEach { student ->
            studentRepo.syncStudentToMongo(student)
        }

        // Sync Attendance to MongoDB
        attendanceRepo.getAllAttendance().first().forEach { record ->
            attendanceRepo.syncAttendanceToMongo(record)
        }

        // Sync Timetables to MongoDB
        timetableRepo.getAllTimetables().first().forEach { timetable ->
            timetableRepo.syncTimetableToMongo(timetable)
        }
    }
}
