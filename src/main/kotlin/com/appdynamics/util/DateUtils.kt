package com.appdynamics.util

import java.time.LocalDateTime

class DateIterator(
    startDate: LocalDateTime,
    private val endDateExclusive: LocalDateTime,
    private val stepMinutes: Long
) : Iterator<LocalDateTime> {

    private var currentDate = startDate
    override fun hasNext() = currentDate < endDateExclusive

    override fun next(): LocalDateTime {
        val next = currentDate
        currentDate = currentDate.plusMinutes(stepMinutes)
        return next
    }
}

class DateProgression(
    override val start: LocalDateTime,
    override val endInclusive: LocalDateTime,
    private val stepMinutes: Long = 1
) :
    Iterable<LocalDateTime>, ClosedRange<LocalDateTime> {
    override fun iterator(): Iterator<LocalDateTime> = DateIterator(start, endInclusive, stepMinutes)
    infix fun step(minutes: Long) = DateProgression(start, endInclusive, minutes)
}

operator fun LocalDateTime.rangeTo(other: LocalDateTime) = DateProgression(this, other)

