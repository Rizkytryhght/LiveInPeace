package com.example.liveinpeace.api

import com.example.liveinpeace.model.SalatTimesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AladhanApiService {
    @GET("timingsByCity")
    fun getTimingsByCity(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int = 20 // Aladhan method
    ): Call<SalatTimesResponse>
}
