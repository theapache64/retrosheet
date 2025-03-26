package com.github.theapache64.retrosheetsample.boil

import com.github.theapache64.retrosheet.annotations.Read
import com.github.theapache64.retrosheetsample.calladapter.flow.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by theapache64 : Aug 09 Sun,2020 @ 10:17
 */
interface Api {

    @Read("SELECT * WHERE group_name = :groupName")
    @GET("groups")
    fun getGroup(
        @Query("groupName") groupName: String
    ): Flow<Resource<Group>>

    @GET
    fun getFile(
        @Url url: String
    ): Flow<Resource<String>>
}
