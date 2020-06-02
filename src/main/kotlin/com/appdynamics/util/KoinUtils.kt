package com.appdynamics.util

import com.appdynamics.api.appdcontroller.*
import com.appdynamics.core.dto.Settings
import mu.KotlinLogging
import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

private val log = KotlinLogging.logger { }

/**
 * Provides KotlinLogging wrapper for Koin.
 * [level] provided NONE and DEFAULT.
 * By DEFAULT, the logger only sends to debug and error.
 */
class KoinLogger(level: Level = Level.INFO) : Logger(level) {

    override fun log(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG, Level.INFO -> log.debug { msg }
            Level.ERROR -> log.error { msg }
            Level.NONE -> return
        }
    }
}

/**
 * Inject dependencies for use in Resiliency Analyzer modules.
 * [args] as path to settings file (maybe other files in future so use an array).
 */
fun injectDependencies(args: Array<String>) {

    val settings: Settings = File(args[0]).jsonToDTO()

    val settingsModule = module {
        single { settings }
    }

    val appdControllerModule = module {
        val userName = "${settings.target.username}@${settings.target.account}"
        val password = settings.target.password
        log.debug { "asd" }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(OkHttp3Logger())
            .addInterceptor(AuthenticationInterceptor(Credentials.basic(userName, password)))
            .addInterceptor(JSONInterceptor())
            .build()
        log.debug { "hmm" }

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(
                (if (settings.target.ssl) "https://" else "http://")
                        + "${settings.target.host}:${settings.target.port}/"
            )
            .client(httpClient)
            .build()

        val applicationService = retrofit.create(IApplicationService::class.java)
        val nodeService = retrofit.create(INodeService::class.java)
        val backendService = retrofit.create(IBackendService::class.java)
        val metricService = retrofit.create(IMetricService::class.java)
        val restUIService = retrofit.create(IRestUIService::class.java)

        single { applicationService }
        single { nodeService }
        single { backendService }
        single { metricService }
        single { restUIService }
    }

    startKoin {
        logger(KoinLogger())
        modules(settingsModule, appdControllerModule)
    }

}
