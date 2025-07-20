package com.projects.attendancemanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.projects.attendancemanager.ui.navigation.NavGraph
import com.projects.attendancemanager.ui.theme.AttendanceManagerTheme
import com.projects.attendancemanager.ui.viewmodel.LoginViewModel
import com.projects.attendancemanager.worker.SyncWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val deepLinkEmail = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        // 👇 Enables icon-based splash screen (Android 12+)
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // 👇 Handle deep link from intent
        handleDeepLink(intent)

        // 👇 Enqueue the first sync manually
        enqueueSyncWorker()

        setContent {
            AttendanceManagerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        loginViewModel = loginViewModel
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null) {
            Log.d("MainActivity", "Deep link received: $data")
            
            // Check if this is a reset password deep link
            if (data.host == "Attendance_Manager" && data.path?.startsWith("/app1/reset-password") == true) {
                val email = data.getQueryParameter("email")
                if (!email.isNullOrEmpty()) {
                    deepLinkEmail.value = email
                    Log.d("MainActivity", "Reset password deep link for email: $email")
                }
            }
        }
    }

    private fun enqueueSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        val workManager = WorkManager.getInstance(this)

        workManager.enqueue(syncRequest)

        // Optional: Log the status of this work
        workManager.getWorkInfoByIdLiveData(syncRequest.id)
            .observe(this) { workInfo ->
                workInfo?.let {
                    when (it.state) {
                        WorkInfo.State.ENQUEUED -> Log.d("SyncWorker", "Sync enqueued")
                        WorkInfo.State.RUNNING -> Log.d("SyncWorker", "Sync running")
                        WorkInfo.State.SUCCEEDED -> Log.d("SyncWorker", "Sync succeeded")
                        WorkInfo.State.FAILED -> Log.d("SyncWorker", "Sync failed")
                        WorkInfo.State.CANCELLED -> Log.d("SyncWorker", "Sync cancelled")
                        else -> Unit
                    }
                }
            }
    }
}
