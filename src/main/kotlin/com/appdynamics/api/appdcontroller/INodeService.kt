package com.appdynamics.api.appdcontroller

import com.appdynamics.api.appdcontroller.dto.Node
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface INodeService {

    @GET("controller/rest/applications/{applicationId}/nodes")
    fun getNodes(
        @Path("applicationId") applicationId: Long
    ): Call<List<Node>>

    @GET("controller/rest/applications/{applicationId}/nodes/{nodeId}")
    fun getNode(
        @Path("applicationId") applicationId: Long,
        @Path("nodeId") nodeId: Long
    ): Call<List<Node>>

}
