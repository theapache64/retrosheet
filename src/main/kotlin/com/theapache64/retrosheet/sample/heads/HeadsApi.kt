package com.theapache64.retrosheet.sample.heads

import com.theapache64.retrofit.calladapter.flow.Resource
import com.theapache64.retrosheet.core.Read
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
}

