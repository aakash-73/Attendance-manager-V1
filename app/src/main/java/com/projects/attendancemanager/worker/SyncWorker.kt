package com.projects.attendancemanager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.projects.attendancemanager.repository.AttendanceRepository
import com.projects.attendancemanager.repository.StudentRepository
import com.projects.attendancemanager.repository.SubjectRepository
import com.projects.attendancemanager.repository.TimetableRepository
import com.projects.attendancemanager.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepository: UserRepository,
    private val subjectRepository: SubjectRepository,
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val timetableRepository: TimetableRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            Log.d("SyncWorker", "Sync started")

            // Sync new or updated users
            userRepository.syncUnsyncedUsers()

            // Sync deleted users (soft-deleted locally, deleted remotely)
            userRepository.syncDeletedUsers()

            // TODO: Add other sync calls for studentRepository, attendanceRepository, etc.

            Log.d("SyncWorker", "Sync completed successfully")

            // Schedule the next sync after a short delay (15 seconds)
            val nextSync = OneTimeWorkRequestBuilder<SyncWorker>()
                .setInitialDelay(15, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(applicationContext).enqueue(nextSync)

            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed", e)

            // On failure, retry after a longer delay (30 seconds)
            val retrySync = OneTimeWorkRequestBuilder<SyncWorker>()
                .setInitialDelay(30, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(applicationContext).enqueue(retrySync)

            Result.failure()
        }
    }
}
