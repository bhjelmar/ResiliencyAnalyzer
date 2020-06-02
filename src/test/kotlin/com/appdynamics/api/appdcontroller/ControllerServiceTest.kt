package com.appdynamics.api.appdcontroller

import com.appdynamics.api.appdcontroller.dto.*
import com.appdynamics.util.KoinLogger
import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import okhttp3.Headers
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response

class ControllerServiceTest : StringSpec({

    val mockControllerService: ControllerService.Companion = mockk()

    val mockApplicationService: IApplicationService = mockk()
    val mockNodeService: INodeService = mockk()
    val mockBackendService: IBackendService = mockk()
    val mockMetricService: IMetricService = mockk()
    val mockRestUIService: IRestUIService = mockk()

    beforeSpec {
        val modules = module {
            single { mockApplicationService }
            single { mockNodeService }
            single { mockBackendService }
            single { mockMetricService }
            single { mockRestUIService }
        }

        startKoin {
            logger(KoinLogger())
            modules(modules)
        }
    }

    "getApplications" {
        // given
        val mockApplication: Application = mockk()
        coEvery { ControllerService.getApplications() } returns listOf(mockApplication)

        // when
        val applications = ControllerService.getApplications()

        // then
        applications.asClue {
            it.size shouldBe 1
            it.first() shouldBe mockApplication
        }
    }

    "getNodes success" {
        // given
        val mockNodesCall: Call<List<Node>> = mockk()
        val mockNodesResponse: Response<List<Node>> = mockk()
        val mockNode: Node = mockk()

        every { mockNodeService.getNodes(any()) } returns mockNodesCall
        every { mockNodesCall.execute() } returns mockNodesResponse
        every { mockNodesResponse.isSuccessful } returns true
        every { mockNodesResponse.body() } returns listOf(mockNode)

        // when
        val nodes = ControllerService.getNodes(1)

        // then
        nodes.asClue {
            it.size shouldBe 1
            it.first() shouldBe mockNode
        }
    }

    "getNodes error" {
        // given
        val mockNodesCall: Call<List<Node>> = mockk()
        val mockNodesResponse: Response<List<Node>> = mockk()

        every { mockNodeService.getNodes(any()) } returns mockNodesCall
        every { mockNodesCall.execute() } returns mockNodesResponse
        every { mockNodesResponse.isSuccessful } returns false
        every { mockNodesResponse.code() } returns 404
        every { mockNodesResponse.message() } returns "foo"

        // when
        val exception = shouldThrow<HttpException> { ControllerService.getNodes(1) }

        // then
        exception.asClue {
            exception.code() shouldBe 404
            exception.message() shouldBe "foo"
        }
    }

    "getNode success" {
        // given
        val mockNodesCall: Call<List<Node>> = mockk()
        val mockNodesResponse: Response<List<Node>> = mockk()
        val mockNode: Node = mockk()

        every { mockNodeService.getNode(any(), any()) } returns mockNodesCall
        every { mockNodesCall.execute() } returns mockNodesResponse
        every { mockNodesResponse.isSuccessful } returns true
        every { mockNodesResponse.body() } returns listOf(mockNode)

        // when
        val nodes = ControllerService.getNode(1, 1)

        // then
        nodes.shouldNotBeNull()
        nodes.asClue {
            it shouldBe mockNode
        }
    }

    "getNode error" {
        // given
        val mockNodesCall: Call<List<Node>> = mockk()
        val mockNodesResponse: Response<List<Node>> = mockk()

        every { mockNodeService.getNode(any(), any()) } returns mockNodesCall
        every { mockNodesCall.execute() } returns mockNodesResponse
        every { mockNodesResponse.isSuccessful } returns false
        every { mockNodesResponse.code() } returns 404
        every { mockNodesResponse.message() } returns "foo"

        // when
        val exception = shouldThrow<HttpException> { ControllerService.getNode(1, 1) }

        // then
        exception.asClue {
            exception.code() shouldBe 404
            exception.message() shouldBe "foo"
        }
    }

    "getBackends success" {
        // given
        val mockBackendsCall: Call<List<Backend>> = mockk()
        val mockBackendsResponse: Response<List<Backend>> = mockk()
        val mockBackend: Backend = mockk()

        every { mockBackendService.getBackends(any()) } returns mockBackendsCall
        every { mockBackendsCall.execute() } returns mockBackendsResponse
        every { mockBackendsResponse.isSuccessful } returns true
        every { mockBackendsResponse.body() } returns listOf(mockBackend)

        // when
        val backends = ControllerService.getBackends(1)

        // then
        backends.shouldNotBeNull()
        backends.asClue {
            it.size shouldBe 1
            it.first() shouldBe mockBackend
        }
    }

    "getBackends error" {
        // given
        val mockBackendsCall: Call<List<Backend>> = mockk()
        val mockBackendsResponse: Response<List<Backend>> = mockk()

        every { mockBackendService.getBackends(any()) } returns mockBackendsCall
        every { mockBackendsCall.execute() } returns mockBackendsResponse
        every { mockBackendsResponse.isSuccessful } returns false
        every { mockBackendsResponse.code() } returns 404
        every { mockBackendsResponse.message() } returns "foo"

        // when
        val exception = shouldThrow<HttpException> { ControllerService.getBackends(1) }

        // then
        exception.asClue {
            exception.code() shouldBe 404
            exception.message() shouldBe "foo"
        }
    }

    "getMetricData success" {
        // given
        val mockMetricsCall: Call<List<MetricData>> = mockk()
        val mockMetricsResponse: Response<List<MetricData>> = mockk()
        val mockMetricData: MetricData = mockk()
        val mockTimeRange: IMetricService.TimeRange = mockk()

        every { mockTimeRange.parameterize() } returns mapOf("foo" to "bar")
        every { mockMetricService.getMetricData(any(), any(), any(), any()) } returns mockMetricsCall
        every { mockMetricsCall.execute() } returns mockMetricsResponse
        every { mockMetricsResponse.isSuccessful } returns true
        every { mockMetricsResponse.body() } returns listOf(mockMetricData)

        // when
        val metricDataNoRollup = ControllerService.getMetricData(1, "foo", mockTimeRange, false)
        val metricDataRollup = ControllerService.getMetricData(1, "foo", mockTimeRange)

        // then
        metricDataNoRollup.shouldNotBeNull()
        metricDataNoRollup.asClue {
            it shouldBe metricDataNoRollup
        }
        metricDataRollup.shouldNotBeNull()
        metricDataRollup.asClue {
            it shouldBe metricDataRollup
        }
    }

    "getMetricData failure" {
        // given
        val mockMetricsCall: Call<List<MetricData>> = mockk()
        val mockMetricsResponse: Response<List<MetricData>> = mockk()
        val mockTimeRange: IMetricService.TimeRange = mockk()

        every { mockTimeRange.parameterize() } returns mapOf("foo" to "bar")
        every { mockMetricService.getMetricData(any(), any(), any(), any()) } returns mockMetricsCall
        every { mockMetricsCall.execute() } returns mockMetricsResponse
        every { mockMetricsResponse.isSuccessful } returns false
        every { mockMetricsResponse.code() } returns 404
        every { mockMetricsResponse.message() } returns "foo"

        // when
        val noRollupException =
            shouldThrow<HttpException> { ControllerService.getMetricData(1, "foo", mockTimeRange, false) }
        val rollupException =
            shouldThrow<HttpException> { ControllerService.getMetricData(1, "foo", mockTimeRange) }

        // then
        noRollupException.asClue {
            noRollupException.code() shouldBe 404
            noRollupException.message() shouldBe "foo"
        }
        rollupException.asClue {
            rollupException.code() shouldBe 404
            rollupException.message() shouldBe "foo"
        }
    }

    "login success" {
        // given
        val mockLoginCall: Call<Void> = mockk()
        val mockLoginResponse: Response<Void> = mockk()
        val mockHeaders: Headers = mockk()
        val mockIterator: Iterator<Pair<String, String>> = mockk()

        every { mockRestUIService.login(any()) } returns mockLoginCall
        every { mockLoginCall.execute() } returns mockLoginResponse
        every { mockLoginResponse.isSuccessful } returns true
        every { mockLoginResponse.headers() } returns mockHeaders
        every { mockHeaders.iterator() } returns mockIterator
        every { mockIterator.next() } returns
                Pair("Set Cookie", "X-CSRF-TOKEN=foo;") andThen
                Pair("Set Cookie", "JSESSIONID=bar;")
        every { mockIterator.hasNext() } returnsMany listOf(true, false, true, false)

        // when
        val login = ControllerService.login()

        // then
        login.shouldNotBeNull()
        login.asClue {
            it.first shouldBe "foo"
            it.second shouldBe "bar"
        }
    }

    "login failure" {
        // given
        val mockLoginCall: Call<Void> = mockk()
        val mockLoginResponse: Response<Void> = mockk()
        val mockHeaders: Headers = mockk()
        val mockIterator: Iterator<Pair<String, String>> = mockk()

        every { mockRestUIService.login(any()) } returns mockLoginCall
        every { mockLoginCall.execute() } returns mockLoginResponse
        every { mockLoginResponse.isSuccessful } returns true
        every { mockLoginResponse.headers() } returns mockHeaders
        every { mockHeaders.iterator() } returns mockIterator
        every { mockIterator.next() } returns
                Pair("Set Cookie", "these are not the droids you're looking for") andThen
                Pair("Set Cookie", "these are not the droids you're looking for")
        every { mockIterator.hasNext() } returnsMany listOf(true, false, true, false)

        // when
        val login = ControllerService.login()

        // then
        login.shouldBeNull()
    }

    "getDatabases success" {
        // given
        val mockDatabasesCall: Call<Databases> = mockk()
        val mockDatabasesResponse: Response<Databases> = mockk()
        val mockDatabases: Databases = mockk()
        val mockDatabase: Databases.Database = mockk()

        every { mockRestUIService.getDatabases(any()) } returns mockDatabasesCall
        every { mockDatabasesCall.execute() } returns mockDatabasesResponse
        every { mockDatabasesResponse.isSuccessful } returns true
        every { mockDatabasesResponse.body() } returns mockDatabases
        every { mockDatabases.data } returns listOf(mockDatabase)

        // when
        val backends = ControllerService.getDatabases(1, 2, 3)

        // then
        backends.shouldNotBeNull()
        backends.asClue {
            it.size shouldBe 1
            it.first() shouldBe mockDatabase
        }
    }

    "getDatabases error" {
        // given
        val mockDatabasesCall: Call<Databases> = mockk()
        val mockDatabasesResponse: Response<Databases> = mockk()

        every { mockRestUIService.getDatabases(any()) } returns mockDatabasesCall
        every { mockDatabasesCall.execute() } returns mockDatabasesResponse
        every { mockDatabasesResponse.isSuccessful } returns false
        every { mockDatabasesResponse.code() } returns 404
        every { mockDatabasesResponse.message() } returns "foo"

        // when
        val exception = shouldThrow<HttpException> { ControllerService.getDatabases(1, 2, 3) }

        // then
        exception.asClue {
            exception.code() shouldBe 404
            exception.message() shouldBe "foo"
        }
    }

})
