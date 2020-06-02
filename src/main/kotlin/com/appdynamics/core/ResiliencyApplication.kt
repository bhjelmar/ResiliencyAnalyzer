package com.appdynamics.core

import com.appdynamics.api.appdcontroller.ControllerService
import com.appdynamics.api.appdcontroller.IMetricService
import com.appdynamics.api.appdcontroller.dto.Application
import com.appdynamics.util.pmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.time.LocalDateTime
import java.time.ZoneId

class ResiliencyApplication(
    private val application: Application,
    iterationStart: LocalDateTime,
    iterationEnd: LocalDateTime
) {
    private val log = KotlinLogging.logger { }

    private val timeRange: IMetricService.TimeRange
    private val iterationStart: Long
    private val iterationEnd: Long

    init {
        log.info { "\tStarting analysis for application ${application.name}" }

        this.iterationStart = iterationStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        this.iterationEnd = iterationEnd.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        timeRange = IMetricService.TimeRange.betweenTimes(this.iterationStart, this.iterationEnd)
    }

    fun gatherData() = runBlocking(Dispatchers.IO) {
        log.debug { "Gathering node calls per minute for application ${application.name}" }
        val nodeCallsPerMinute = ControllerService.getMetricData(
            application.id,
            "Overall Application Performance|*|Individual Nodes|*|Calls per Minute",
            timeRange
        )
        log.debug { "Gathering backend calls per minute for application ${application.name}" }
        val backendCallsPerMinute = ControllerService.getMetricData(
            application.id,
            "Backends|*|Calls per Minute",
            timeRange
        )

        log.debug { "Gathering nodes for application ${application.name}" }
        val nodeData = ControllerService.getNodes(application.id)
            .also { log.debug { "Gathering individual node information for application ${application.name}" } }
            .pmap { ControllerService.getNode(application.id, it.id) }
            .filterNotNull()
            .pmap { node ->
                Pair(
                    node,
                    nodeCallsPerMinute
                        .firstOrNull { it.metricPath.contains(node.name) }
                        ?.metricValues?.getOrNull(0)
                )
            }
        log.debug { "Gathering backends for application ${application.name}" }
        // We require two calls for databases.
        // The first call yields limited information but will automatically filter remote services of type Database.
        // The second call yields all remote services complete with their IP addresses.
        // The info we need is the intersection of these two.
        val databaseIDs = ControllerService.getDatabases(application.id, iterationStart, iterationEnd).map { it.id }
        val backendData = ControllerService.getBackends(application.id)
            .filter { databaseIDs.contains(it.id) }
            .pmap { backend ->
                Pair(
                    backend,
                    backendCallsPerMinute
                        .firstOrNull { it.metricPath.contains(backend.name) }
                        ?.metricValues?.getOrNull(0)
                )
            }

        nodeData.forEach {
            log.info { "\t\tNode ${it.first.name} - ${it.first.ipAddresses} - ${it.second?.sum}" }
        }
        backendData.forEach {
            log.info { "\t\tBackend ${it.first.name} - ${it.first.properties.firstOrNull { it.name == "HOST" }?.value} - ${it.second?.sum}" }
        }


    }

}
