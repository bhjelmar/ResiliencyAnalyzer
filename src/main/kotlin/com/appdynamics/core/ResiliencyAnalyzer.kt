package com.appdynamics.core

import com.appdynamics.api.appdcontroller.dto.Application
import java.time.LocalDateTime

class ResiliencyAnalyzer {

    companion object {
        fun analyze(
            application: Application,
            iterationStart: LocalDateTime,
            iterationEnd: LocalDateTime
        ) {

            val resiliencyApplication = ResiliencyApplication(application, iterationStart, iterationEnd)
            resiliencyApplication.gatherData()
        }
    }

    enum class ResiliencyStatus {
        NONE,
        STARTED,
        CONTINUES,
        ENDED,
        CANCELLED
    }

}
