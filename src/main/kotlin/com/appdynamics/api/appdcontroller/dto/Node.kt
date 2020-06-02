package com.appdynamics.api.appdcontroller.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Node(
    val appAgentVersion: String,
    val machineAgentVersion: String,
    val agentType: String,
    val type: String,
    val machineName: String,
    val appAgentPresent: Boolean,
    val nodeUniqueLocalId: String,
    val machineId: Long,
    val machineOSType: String,
    val tierId: Long,
    val tierName: String,
    val machineAgentPresent: Boolean,
    val name: String,
    val ipAddresses: IpAddresses?,
    val id: Long
) {
    @JsonClass(generateAdapter = true)
    data class IpAddresses(
        val ipAddresses: List<String>
    )
}