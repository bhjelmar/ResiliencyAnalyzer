package com.appdynamics.api.appdcontroller

import com.appdynamics.api.appdcontroller.dto.*
import com.appdynamics.util.getList
import com.appdynamics.util.getSingle
import mu.KotlinLogging
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException

/**
 * Service class for querying an AppDynamics controller.
 */
class ControllerService {

    companion object : KoinComponent {
        private val log = KotlinLogging.logger { }

        private val applicationService: IApplicationService by inject()
        private val nodeService: INodeService by inject()
        private val backendService: IBackendService by inject()
        private val metricService: IMetricService by inject()
        private val restUIService: IRestUIService by inject()

        /**
         * Get all applications on controller.
         */
        suspend fun getApplications(): List<Application> = applicationService.getApplications()

        /**
         * Get all nodes for [applicationId].
         */
        fun getNodes(applicationId: Long): List<Node> = getList(nodeService.getNodes(applicationId))

        /**
         * Get node for [applicationId] and [nodeId].
         */
        fun getNode(applicationId: Long, nodeId: Long): Node? = getList(nodeService.getNode(applicationId, nodeId))[0]

        /**
         * Get backends for [applicationId].
         */
        fun getBackends(applicationId: Long): List<Backend> = getList(backendService.getBackends(applicationId))

        /**
         * Get [metricPath] for [applicationId] at [timeRange].
         * If [rollup] is false, separate results for all values within the time range will be returned.
         * Else (by default), all data will be rolled up into a single data point.
         */
        fun getMetricData(
            applicationId: Long,
            metricPath: String,
            timeRange: IMetricService.TimeRange,
            rollup: Boolean = true
        ): List<MetricData> {
            return getList(metricService.getMetricData(applicationId, rollup, metricPath, timeRange.parameterize()))
        }

        fun getDatabases(applicationId: Long, startTime: Long, endTime: Long): List<Databases.Database> {
            val json = """
                    {
                    	"requestFilter": {
                    		"queryParams": {
                    			"applicationId": $applicationId
                    		},
                    		"filters": []
                    	},
                    	"resultColumns": ["ID", "NAME", "TYPE"],
                    	"offset": 0,
                    	"limit": -1,
                    	"searchFilters": [],
                    	"columnSorts": [],
                    	"timeRangeStart": $startTime,
                    	"timeRangeEnd": $endTime
                    }
                """.trimIndent()
            val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            return getSingle(restUIService.getDatabases(body))?.data ?: emptyList()
        }

        fun login(): Pair<String, String>? {
            val response = restUIService.login().execute()
            if (response.isSuccessful) {
                val xcsrftoken = response.headers()
                    .filter { it.second.contains("X-CSRF-TOKEN") }
                    .map {
                        it.second.substringAfter("X-CSRF-TOKEN=").substringBefore(";")
                    }.firstOrNull()
                val jsessionid = response.headers()
                    .filter { it.second.contains("JSESSIONID") }
                    .map {
                        it.second.substringAfter("JSESSIONID=").substringBefore(";")
                    }.firstOrNull()
                if (jsessionid == null || xcsrftoken == null) {
                    log.error { "Failed to retrieve login credentials from login request." }
                    return null
                }
                return Pair(xcsrftoken, jsessionid)
            } else {
                throw HttpException(response)
            }
        }

    }

}
