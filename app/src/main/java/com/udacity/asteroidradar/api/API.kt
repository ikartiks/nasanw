package com.udacity.asteroidradar.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface API {

    @GET("neo/rest/v1/feed?api_key=cLAvw2AHLKnGYEhcf8n6JlN2usbuIx4VFYGgzNxX")
    fun  getAsteroidsFor7Days(@Query("start_date") startDate:String,@Query("end_date") endDate:String ): Call<ResponseBody>
}