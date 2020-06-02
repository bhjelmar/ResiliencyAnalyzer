package com.appdynamics.api.appdcontroller.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Application(
    val name: String,
    val description: String,
    val id: Long,
    val accountGuid: String
)