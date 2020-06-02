package com.appdynamics.api.appdcontroller.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetricData(
    val frequency: String,
    val metricId: Int,
    val metricName: String,
    val metricPath: String,
    val metricValues: List<MetricValue>
) {
    @JsonClass(generateAdapter = true)
    data class MetricValue(
        val count: Int,
        val current: Int,
        val max: Int,
        val min: Int,
        val occurrences: Int,
        val standardDeviation: Int,
        val startTimeInMillis: Long,
        val sum: Int,
        val useRange: Boolean,
        val value: Int
    )
}
