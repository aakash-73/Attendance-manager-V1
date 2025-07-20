package com.projects.attendancemanager.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.projects.attendancemanager.repository.AttendanceRepository
import com.projects.attendancemanager.repository.StudentRepository
import com.projects.attendancemanager.repository.SubjectRepository
import com.projects.attendancemanager.repository.TimetableRepository
import com.projects.attendancemanager.repository.UserRepository
import javax.inject.Inject

class SyncWorkerFactory @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val timetableRepository: TimetableRepository,
    private val userRepository: UserRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncWorker::class.java.name -> {
                SyncWorker(
                    appContext,
                    workerParameters,
                    userRepository,
                    subjectRepository,
                    studentRepository,
                    attendanceRepository,
                    timetableRepository
                )
            }
            else -> null
        }
    }
}