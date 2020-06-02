package com.appdynamics.api.appdcontroller

import com.appdynamics.api.appdcontroller.dto.MetricData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface IMetricService {

    @GET("/controller/rest/applications/{applicationId}/metric-data")
    fun getMetricData(
        @Path("applicationId") applicationId: Long,
        @Query("rollup") rollup: Boolean,
        @Query("metric-path") metricPath: String,
        @QueryMap timeRange: Map<String, String>
    ): Call<List<MetricData>>

    /**
     * Holds relavent information for [type] of [TimeRange], along with its [parameters].
     */
    class TimeRange private constructor(
        private val type: Type,
        private val parameters: List<Long>
    ) {
        companion object {
            /**
             * Returns a [TimeRange] of type [Type.BEFORE_NOW] with [durationInMins].
             */
            fun beforeNow(durationInMins: Long): TimeRange =
                TimeRange(Type.BEFORE_NOW, listOf(durationInMins))

            /**
             * Returns a [TimeRange] of type [Type.BEFORE_TIME] with [durationInMins] and [endTime].
             */
            fun beforeTime(durationInMins: Long, endTime: Long): TimeRange =
                TimeRange(Type.BEFORE_TIME, listOf(durationInMins, endTime))

            /**
             * Returns a [TimeRange] of type [Type.AFTER_TIME] with [durationInMins] and [startTime].
             */
            fun afterTime(durationInMins: Long, startTime: Long): TimeRange =
                TimeRange(Type.AFTER_TIME, listOf(durationInMins, startTime))

            /**
             * Returns a [TimeRange] of type [Type.BETWEEN_TIMES] with [startTime] and [endTime].
             */
            fun betweenTimes(startTime: Long, endTime: Long): TimeRange =
                TimeRange(Type.BETWEEN_TIMES, listOf(startTime, endTime))
        }

        /**
         * Enum specifying [type] of time range, as well as expected [parameters] for this time range type.
         */
        private enum class Type(
            val type: String,
            val parameters: List<String>
        ) {
            BEFORE_NOW("BEFORE_NOW", listOf("duration-in-mins")),
            BEFORE_TIME("BEFORE_TIME", listOf("duration-in-mins", "end-time")),
            AFTER_TIME("AFTER_TIME", listOf("duration-in-mins", "start-time")),
            BETWEEN_TIMES("BETWEEN_TIMES", listOf("start-time", "end-time"))
        }

        /**
         * Gathers all [parameters] of the time range into a map.
         * AppDynamics expects time range [parameters] to be passed in this way to the metrics API.
         */
        fun parameterize(): Map<String, String> {
            val map = mutableMapOf("time-range-type" to type.type)
            map.putAll(type.parameters.zip(parameters.map { it.toString() }).toMap())
            return map
        }
    }

}
