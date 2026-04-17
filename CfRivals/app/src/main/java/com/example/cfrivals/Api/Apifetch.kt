package com.example.cfrivals.Api

import com.example.cfrivals.Models.CFResponse
import com.example.cfrivals.Models.Submission
import com.example.cfrivals.Models.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CodeforcesApi {
    // Fetches info for multiple users at once (separated by ;)
    @GET("user.info")
    suspend fun getUsers(@Query("handles") handles: String): Response<CFResponse<User>>

    // Fetches latest submissions for a specific handle
    @GET("user.status")
    suspend fun getStatus(
        @Query("handle") handle: String,
        @Query("from") from: Int = 1,
        @Query("count") count: Int = 50
    ): Response<CFResponse<Submission>>
}