package com.example.bvgrecruitmenttask.data.mapper

import com.example.bvgrecruitmenttask.data.EventType
import com.example.bvgrecruitmenttask.data.eventresponse.AccountResponse
import com.example.bvgrecruitmenttask.data.eventresponse.ServerSentEventResponse
import com.example.bvgrecruitmenttask.domain.time.CurrentTimeProvider
import com.squareup.moshi.JsonAdapter
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ServerSentEventResponseMapperTest {
    private val adapter: JsonAdapter<ServerSentEventResponse> = mockk()
    private val currentTimeProvider: CurrentTimeProvider = mockk()
    private lateinit var sut: ServerSentEventResponseMapper

    @Before
    fun setup() {
        sut =
            ServerSentEventResponseMapper(
                adapter = adapter,
                currentTimeProvider = currentTimeProvider,
            )
    }

    @Test
    fun `Given valid JSON and type When mapEventResponse is called Then returns mapped Event`() {
        val json = """{"id": "12", "createdAt": "12331432", "account": {"username": "username"}}"""
        val eventResponse =
            ServerSentEventResponse(
                id = "12",
                createdAt = "12331432",
                account = AccountResponse("username"),
            )
        every { adapter.fromJson(json) } returns eventResponse
        every { currentTimeProvider.currentTimeMillis() } returns 123123L

        val result = sut.mapEventResponse(json = json, type = "update")

        assertEquals("12", result?.id)
        assertEquals("12331432", result?.createdAt)
        assertEquals(EventType.Update, result?.eventType)
        assertEquals("username", result?.account?.username)
        assertEquals(123123L, result?.timestamp)
    }

    @Test
    fun `Given invalid JSON When mapEventResponse is called Then returns null`() {
        val invalidJson = """{invalidJson}"""
        every { adapter.fromJson(invalidJson) } throws Exception("Malformed JSON")

        val result = sut.mapEventResponse(json = invalidJson, type = "update")

        assertNull(result)
    }

    @Test
    fun `Given valid JSON but unknown type When mapEventResponse is called Then returns Event with Unknown EventType`() {
        val json = """{"id": "12", "createdAt": "12331432", "account": {"username": "username"}}"""
        val eventResponse =
            ServerSentEventResponse(
                id = "12",
                createdAt = "12331432",
                account = AccountResponse("username"),
            )
        every { adapter.fromJson(json) } returns eventResponse
        every { currentTimeProvider.currentTimeMillis() } returns 123123L

        val result = sut.mapEventResponse(json = json, type = "unknown")

        assertEquals("12", result?.id)
        assertEquals(EventType.Unknown, result?.eventType)
    }
}
