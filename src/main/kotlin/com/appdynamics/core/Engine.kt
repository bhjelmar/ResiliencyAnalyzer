package com.appdynamics.core

import com.appdynamics.api.appdcontroller.ControllerService
import com.appdynamics.core.dto.Settings
import com.appdynamics.util.rangeTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.knowm.sundial.Job
import org.knowm.sundial.annotations.CronTrigger
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

const val HOUR = "0 0 * ? * *"
const val MINUTE = "0 * * ? * *"

@CronTrigger(cron = HOUR, timeZone = "UTC")
class Engine : KoinComponent, Job() {

    private val log = KotlinLogging.logger { }

    private val settings: Settings by inject()

    override fun doRun() = runBlocking(Dispatchers.IO) {
        val engineEndTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        val engineStartTime = engineEndTime.minusHours(1)

        log.info { "Starting analysis from $engineStartTime to $engineEndTime." }

        val applications = ControllerService.getApplications()
            .filter { settings.target.applications.contains(it.name) }
            .also { log.debug { "Filtered ${it.size} applications." } }

        for (iterationStart in engineStartTime..engineEndTime step settings.analysisGranularityMinutes) {
            val iterationEnd = iterationStart.plusMinutes(settings.analysisGranularityMinutes)

            log.info { "Analyzing time step $iterationStart to $iterationEnd." }
//            applications.forEach {
//                ResiliencyAnalyzer.analyze(it, iterationStart, iterationEnd)
//            }
        }
    }
}
