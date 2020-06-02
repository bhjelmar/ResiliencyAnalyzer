package com.appdynamics

import com.appdynamics.core.Engine
import com.appdynamics.util.injectDependencies
import com.appdynamics.util.readFileFromResources
import mu.KotlinLogging
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val log = KotlinLogging.logger { }

// ways to specify data center mapping
// static file, supply DB connection
//      App Name    Data Center Name    IP Range                    Lat/Long
//      App1,       DC1,                10.11.0.0-10.11.255.255     125/123
//      App1,       DC2,                10.12.0.0-10.12.255.255     843/412

@ExperimentalTime
fun main(args: Array<String>) {

    log.info { "\n${readFileFromResources("/splash.txt")}" }
    log.info { "Initializing Resiliency Analyzer" }

    if (args.size != 1) {
        log.error { "Invalid startup arguments." }
        log.error { "Expected settings.json as arg[0] an nothing else." }
        return
    }

    injectDependencies(args)

    log.info { "Initialized Resiliency Analyzer successfully." }
    log.info { "Will analyze data for previous hour, every hour, on the hour." }

//    startScheduler("com.appdynamics.core")
    val time = measureTime { Engine().doRun() }
    log.info { "Execution time: $time" }
}
