package com.appdynamics.api.appdcontroller

import com.appdynamics.api.appdcontroller.dto.Application
import retrofit2.http.GET

interface IApplicationService {

    @GET("/controller/rest/applications")
    suspend fun getApplications(): List<Application>

}
