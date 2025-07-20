package com.projects.attendancemanager.network

import com.projects.attendancemanager.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

data class ResetPasswordRequest(val email: String)
data class ResetPasswordSubmitRequest(val email: String, val newPassword: String)
data class ResetPasswordResponse(
    val message: String,
    val hashedPassword: String
)

interface UserApiService {
    @POST("/api/users")
    suspend fun registerUserToDb(@Body user: UserDto): Response<Unit>

    @DELETE("/api/users/{id}")
    suspend fun deleteUserFromDb(@Path("id") id: String): Response<Unit>

    @POST("/api/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>

    @POST("/api/reset-password/submit")
    suspend fun submitNewPassword(@Body request: ResetPasswordSubmitRequest): Response<ResetPasswordResponse>
}
