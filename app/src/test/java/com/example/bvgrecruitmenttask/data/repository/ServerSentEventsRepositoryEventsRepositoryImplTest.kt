package com.example.bvgrecruitmenttask.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.bvgrecruitmenttask.MainCoroutineRule
import com.example.bvgrecruitmenttask.data.EventType
import com.example.bvgrecruitmenttask.data.mapper.ServerSentEventResponseMapper
import com.example.bvgrecruitmenttask.data.requestfactory.ACCESS_TOKEN
import com.example.bvgrecruitmenttask.data.requestfactory.RequestFactory
import com.example.bvgrecruitmenttask.domain.model.Account
import com.example.bvgrecruitmenttask.domain.model.Event
import com.example.bvgrecruitmenttask.domain.time.CurrentTimeProvider
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ServerSentEventsRepositoryEventsRepositoryImplTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    private val client: OkHttpClient = OkHttpClient.Builder().build()
    private val currentTimeProvider: CurrentTimeProvider = mockk()
    private val requestFactory: RequestFactory = mockk()
    private val mapper: ServerSentEventResponseMapper = mockk()
    private val trueRequest = Request
        .Builder()
        .url("https://mas.to/api/v1/streaming/public")
        .header("Accept", "application/json; q=0.5")
        .addHeader("Accept", "text/event-stream")
        .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
        .build()
    private lateinit var sut: ServerSentEventsRepositoryEventsRepositoryImpl

    @Before
    fun setup() {
        sut = ServerSentEventsRepositoryEventsRepositoryImpl(
            client = client,
            currentTimeProvider = currentTimeProvider,
            requestFactory = requestFactory,
            mapper = mapper
        )
    }

    @Test
    fun `Given correct host Then flow emits`() {

        every { currentTimeProvider.currentTimeMillis() } returns 123123L
        every { requestFactory.createSseRequest() } returns trueRequest
        every { mapper.mapEventResponse(json = any(), type = any()) } returns Event(
            id = "12",
            eventType = EventType.Delete,
            account = Account("username"),
            timestamp = 123123L
        )

        runTest {
            sut.eventFlow.test {
                val item = awaitItem()
                assertEquals("12", item.id)
                assertEquals("username", item.account?.username)
                assertEquals(EventType.Delete, item.eventType)
            }
        }
    }

    @Test
    fun `Given valid host Then flow emits valid Event`() {
        every { currentTimeProvider.currentTimeMillis() } returns 123123L
        every { requestFactory.createSseRequest() } returns trueRequest
        every { mapper.mapEventResponse(json = "", type = "") } returns Event(
            "12",
            "12331432",
            EventType.Delete,
            Account("username"),
            timestamp = 123123L
        )

        runTest {
            sut.eventFlow.test {
                awaitItem()
            }
        }
    }
}
