package com.projects.attendancemanager.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.projects.attendancemanager.db.database.AttendanceDatabase
import com.projects.attendancemanager.data.dao.*
import com.projects.attendancemanager.network.UserApiService
import com.projects.attendancemanager.repository.*
import com.projects.attendancemanager.worker.SyncWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AttendanceDatabase {
        return Room.databaseBuilder(
            context,
            AttendanceDatabase::class.java,
            "attendance_manager_database"
        ).fallbackToDestructiveMigration()  // Add fallback to handle schema migrations
            .build()
    }

    @Provides
    @Singleton
    fun provideSubjectDao(database: AttendanceDatabase): SubjectDao {
        return database.subjectDao()
    }

    @Provides
    @Singleton
    fun provideStudentDao(database: AttendanceDatabase): StudentDao {
        return database.studentDao()
    }

    @Provides
    @Singleton
    fun provideAttendanceDao(database: AttendanceDatabase): AttendanceDao {
        return database.attendanceDao()
    }

    @Provides
    @Singleton
    fun provideTimetableDao(database: AttendanceDatabase): TimetableDao {
        return database.timetableDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AttendanceDatabase): UserDao {
        return database.userDao()  // Make sure userDao() is implemented in AttendanceDatabase
    }

    @Provides
    @Singleton
    fun provideSubjectRepository(subjectDao: SubjectDao): SubjectRepository {
        return SubjectRepository(subjectDao)
    }

    @Provides
    @Singleton
    fun provideStudentRepository(studentDao: StudentDao): StudentRepository {
        return StudentRepository(studentDao)
    }

    @Provides
    @Singleton
    fun provideAttendanceRepository(attendanceDao: AttendanceDao): AttendanceRepository {
        return AttendanceRepository(attendanceDao)
    }

    @Provides
    @Singleton
    fun provideTimetableRepository(timetableDao: TimetableDao): TimetableRepository {
        return TimetableRepository(timetableDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(@ApplicationContext context: Context, userDao: UserDao, userApiService: UserApiService): UserRepository {
        return UserRepository(context, userDao, userApiService)
    }

    @Provides
    @Singleton
    fun provideUserApiService(): UserApiService {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") // Replace with your actual backend URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideSyncWorkerFactory(
        subjectRepository: SubjectRepository,
        studentRepository: StudentRepository,
        attendanceRepository: AttendanceRepository,
        timetableRepository: TimetableRepository,
        userRepository: UserRepository
    ): SyncWorkerFactory {
        return SyncWorkerFactory(
            subjectRepository,
            studentRepository,
            attendanceRepository,
            timetableRepository,
            userRepository
        )
    }
}
