package com.appdynamics.api.appdcontroller

import com.appdynamics.api.appdcontroller.dto.Databases
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IRestUIService {

    @GET("controller/auth")
    fun login(
        @Query("action") action: String = "login"
    ): Call<Void>

    @POST("controller/restui/backend/list/database")
    fun getDatabases(
        @Body body: RequestBody
    ): Call<Databases>

}
