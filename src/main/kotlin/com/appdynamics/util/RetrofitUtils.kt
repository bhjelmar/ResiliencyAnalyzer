package com.appdynamics.util

import com.appdynamics.api.appdcontroller.ControllerService
import mu.KotlinLogging
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger { }

/**
 * Execute [call] which is expected to return a single JSONObject of type [T].
 */
fun <T> getSingle(call: retrofit2.Call<T>): T? {
    val response: Response<T> = call.execute()
    return if (response.isSuccessful) {
        response.body()
    } else {
        throw HttpException(response)
    }
}

/**
 * Execute [call] which is expected to return a JSONArray of type [T].
 */
fun <T> getList(call: retrofit2.Call<List<T>>): List<T> {
    val response: Response<List<T>> = call.execute()
    return if (response.isSuccessful) {
        response.body() ?: emptyList()
    } else {
        throw HttpException(response)
    }
}

/**
 * Interceptor which applies [authToken] as authorization header to an OkHttp3 request.
 */
class AuthenticationInterceptor(private val authToken: String) : Interceptor {
    /**
     * Intercepts request [chain] to append authentication headers.
     */
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val original: Request = chain.request()
        val builder: Request.Builder
        if (original.url.toString().contains("restui")) {
            ControllerService.login()?.let {
                builder = original.newBuilder()
                    .header("X-CSRF-TOKEN", it.first)
                    .header("Cookie", "JSESSIONID=${it.second}; X-CSRF-TOKEN=${it.first};")
                    .header("Content-Type", "application/json;charset=UTF-8")
                val request: Request = builder.build()
                return chain.proceed(request)
            }
        } else {
            builder = original.newBuilder().header("Authorization", authToken)
            val request: Request = builder.build()
            return chain.proceed(request)
        }
        return chain.proceed(original)
    }
}

/**
 * Interceptor which applies arbitrary query parameters to an OkHttp3 request chain.
 */
class JSONInterceptor : Interceptor {
    /**
     * Intercepts request [chain] to append parameters.
     */
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val original: Request = chain.request()
        val url: HttpUrl = original.url.newBuilder().addQueryParameter("output", "json").build()
        val request: Request = original.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}

/**
 * Provides [KotlinLogging] wrapper for OkHttp3.
 * DEFAULT log [level] directs to debug.
 * NONE log [level] logs nothing.
 */
class OkHttp3Logger(private val level: Level = Level.DEFAULT) : Interceptor {

    enum class Level {
        NONE,
        DEFAULT
    }

    /**
     * Intercepts [chain] to debug diagnostic data.
     */
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val level = this.level

        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }

        val connection = chain.connection()

        log.debug { "--> ${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}" }

        val startNs = System.nanoTime()
        val response: okhttp3.Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            log.error { ("<-- HTTP FAILED: $e") }
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body
        val contentLength = responseBody?.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        log.debug { "<-- ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} ${tookMs}ms, $bodySize body" }

        return response
    }

}
