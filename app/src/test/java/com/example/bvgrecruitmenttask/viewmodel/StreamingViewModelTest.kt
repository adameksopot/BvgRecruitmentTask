package com.example.bvgrecruitmenttask.viewmodel

import app.cash.turbine.test
import com.example.bvgrecruitmenttask.MainCoroutineRule
import com.example.bvgrecruitmenttask.data.EventType
import com.example.bvgrecruitmenttask.domain.model.Account
import com.example.bvgrecruitmenttask.domain.model.Event
import com.example.bvgrecruitmenttask.domain.repository.ServerSentEventsRepository
import com.example.bvgrecruitmenttask.domain.time.CurrentTimeProvider
import com.example.bvgrecruitmenttask.presentation.StreamingViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class StreamingViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var sut: StreamingViewModel
    private val repository: ServerSentEventsRepository = mockk()
    private val currentTimeProvider: CurrentTimeProvider = mockk()

    @Before
    fun setup() {
        sut = StreamingViewModel(repository, currentTimeProvider)
    }

    init {
        every { currentTimeProvider.currentTimeMillis() } returns 123123L
    }

    @Test
    fun `Given event flow emits an event Then state should contain the emitted event`() {
        val expectedItem = Event(eventType = EventType.Delete, id = "12", timestamp = currentTimeProvider.currentTimeMillis())
        coEvery { repository.eventFlow } returns
            flowOf(
                Event(
                    eventType = EventType.Delete,
                    id = "12",
                    timestamp = currentTimeProvider.currentTimeMillis(),
                ),
            )
        sut.collectEvents()
        runTest {
            sut.state.test {
                val initialState = awaitItem()
                assertEquals(sut.state.value, initialState)
                val updatedState = awaitItem()
                // I am checking event types in order to do not mockk System.getCurrentTimeMillis()
                assertEquals(expectedItem.eventType, updatedState.first().eventType)
            }
        }
    }

    @Test
    fun `Given error event Then state should be error`() {
        val expectedItem = Event(eventType = EventType.Error, timestamp = currentTimeProvider.currentTimeMillis())

        runTest {
            coEvery { repository.eventFlow } returns flow { throw IOException() }
            sut.collectEvents()
            sut.state.test {
                val item = awaitItem()
                assertEquals(sut.state.value, item)
                val itemAnother = awaitItem()
                // I am checking event types in order to do not mockk System.getCurrentTimeMillis()
                assertEquals(expectedItem.eventType, itemAnother.first().eventType)
            }
        }
    }

    @Test
    fun `Given a list of events When search query is applied Then filtered state should contain matching events`() {
        val events =
            listOf(
                Event(
                    eventType = EventType.Update,
                    id = "1",
                    account = Account("Luke Skywalker"),
                    timestamp = currentTimeProvider.currentTimeMillis(),
                ),
                Event(
                    eventType = EventType.Update,
                    id = "2",
                    account = Account("Darth Vader"),
                    timestamp = currentTimeProvider.currentTimeMillis(),
                ),
                Event(
                    eventType = EventType.Update,
                    id = "3",
                    account = Account("Leia Organa"),
                    timestamp = currentTimeProvider.currentTimeMillis(),
                ),
                Event(
                    eventType = EventType.Update,
                    id = "4",
                    account = Account("HanSolo"),
                    timestamp = currentTimeProvider.currentTimeMillis(),
                ),
                Event(
                    eventType = EventType.Update,
                    id = "5",
                    account = Account("Obi Wan Kenobi"),
                    timestamp = currentTimeProvider.currentTimeMillis(),
                ),
            )

        coEvery { repository.eventFlow } returns flowOf(*events.toTypedArray())

        sut.collectEvents()
        sut.onSearchQuery("Vader")

        runTest {
            sut.state.test {
                val initialState = awaitItem()
                assertTrue(initialState.isEmpty())

                val updatedState = awaitItem()
                val matchingEvents =
                    updatedState.filter {
                        it.account?.username?.contains(
                            "Vader",
                            ignoreCase = true,
                        ) == true
                    }

                assertEquals(1, matchingEvents.size)
                assertEquals("Darth Vader", matchingEvents.first().account?.username)
            }
        }
    }
}
