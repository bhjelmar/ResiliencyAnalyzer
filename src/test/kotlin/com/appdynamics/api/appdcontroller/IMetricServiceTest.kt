package com.appdynamics.api.appdcontroller

import io.kotest.assertions.asClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal class IMetricServiceTest : StringSpec({

    "beforeNow parameterizes correctly" {
        val afterTime = IMetricService.TimeRange.beforeNow(1)
        afterTime.asClue {
            it.parameterize() shouldBe mapOf(
                "time-range-type" to "BEFORE_NOW",
                "duration-in-mins" to "1"
            )
        }
    }

    "beforeTime parameterizes correctly" {
        val afterTime = IMetricService.TimeRange.beforeTime(1, 2)
        afterTime.asClue {
            it.parameterize() shouldBe mapOf(
                "time-range-type" to "BEFORE_TIME",
                "duration-in-mins" to "1",
                "end-time" to "2"
            )
        }
    }

    "afterTime parameterizes correctly" {
        val afterTime = IMetricService.TimeRange.afterTime(1, 2)
        afterTime.asClue {
            it.parameterize() shouldBe mapOf(
                "time-range-type" to "AFTER_TIME",
                "duration-in-mins" to "1",
                "start-time" to "2"
            )
        }
    }

    "betweenTimes parameterizes correctly" {
        val afterTime = IMetricService.TimeRange.betweenTimes(1, 2)
        afterTime.asClue {
            it.parameterize() shouldBe mapOf(
                "time-range-type" to "BETWEEN_TIMES",
                "start-time" to "1",
                "end-time" to "2"
            )
        }
    }

})