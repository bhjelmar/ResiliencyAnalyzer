package com.appdynamics.core.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Settings(
    val analysisGranularityMinutes: Long,
    val target: Target
) {
    @JsonClass(generateAdapter = true)
    data class Target(
        val host: String,
        val port: Int,
        val ssl: Boolean,
        val account: String,
        val username: String,
        val password: String,
        val applications: List<String>
    )
}
