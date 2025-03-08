package com.github.theapache64.retrosheetsample.heads

import com.github.theapache64.retrofit.calladapter.flow.Resource
import com.github.theapache64.retrosheet.annotations.Read
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface HeadsApi {

    @Read("SELECT * WHERE username= :username AND password= :password ")
    @GET("users")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Flow<Resource<User>>

    @Read("SELECT *")
    @GET("users")
    fun getAllUsers(): Flow<Resource<List<User>>>
}
