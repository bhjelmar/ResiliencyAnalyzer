package com.appdynamics.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import mu.KotlinLogging
import java.io.File


/**
 * Demarshalls [this] from JSON to object of type [T].
 */
inline fun <reified T> File.jsonToDTO(): T {
    val log = KotlinLogging.logger { }

    log.debug { "Parsing ${this.absolutePath} to ${T::class.java}" }
    return Moshi.Builder()
        .add(KotlinJsonAdapterFactory()).build()
        .adapter(T::class.java)
        .fromJson(this.readText(Charsets.UTF_8))!!
}

/**
 * Reads [fileName] from resources.
 */
fun readFileFromResources(fileName: String) = {}::javaClass::class.java.getResource(fileName).readText()
