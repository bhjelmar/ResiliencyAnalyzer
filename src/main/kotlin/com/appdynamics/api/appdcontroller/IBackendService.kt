package com.appdynamics.api.appdcontroller

import com.appdynamics.api.appdcontroller.dto.Backend
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface IBackendService {

    @GET("controller/rest/applications/{applicationId}/backends")
    fun getBackends(
        @Path("applicationId") applicationId: Long
    ): Call<List<Backend>>

}
