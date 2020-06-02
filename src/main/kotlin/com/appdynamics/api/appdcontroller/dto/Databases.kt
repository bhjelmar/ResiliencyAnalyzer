package com.appdynamics.api.appdcontroller.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Databases(
    val data: List<Database>
) {
    @JsonClass(generateAdapter = true)
    data class Database(
        val id: Long,
        val name: String,
        val exitPointSubtype: String,
        val doNotResolve: Boolean,
        val performanceStats: Boolean?,
        val dbBackendStatus: String
    )
}
