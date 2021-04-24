package com.github.theapache64.retrosheet.bugs

import com.github.theapache64.retrosheet.core.Read
import com.github.theapache64.retrosheet.core.ReadAsList
import retrofit2.http.GET

internal interface NetworkingService {

    @ReadAsList
    @Read("SELECT *")
    @GET(NozzleTypeResponse.SHEET_NAME)
    suspend fun getNozzleTypes(): List<NozzleTypeResponse>

    @Read("SELECT *")
    @GET(NozzleStubResponse.SHEET_NAME)
    suspend fun getNozzles(): List<NozzleStubResponse>
}