package com.appdynamics.api.appdcontroller.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Backend(
    val exitPointType: String,
    val tierId: Long,
    val name: String,
    val applicationComponentNodeId: Long,
    val id: Long,
    val properties: List<Properties>
) {
    @JsonClass(generateAdapter = true)
    data class Properties(
        val name: String,
        val id: Long,
        val value: String
    )
}