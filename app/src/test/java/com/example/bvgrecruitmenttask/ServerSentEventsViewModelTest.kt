package com.example.bvgrecruitmenttask

import app.cash.turbine.test
import com.example.bvgrecruitmenttask.data.EventType
import com.example.bvgrecruitmenttask.domain.model.Event
import com.example.bvgrecruitmenttask.domain.repository.ServerSentEventsRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ServerSentEventsViewModelTest {


    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    lateinit var sut: ServerSentEventsViewModel
    private val repository: ServerSentEventsRepository = mockk()

    @Before
    fun setup() {
        sut = ServerSentEventsViewModel(repository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given event flow emits an event Then state should contain the emitted event`() {
        val expectedItem = Event(eventType = EventType.Delete, id = "12")
        coEvery { repository.eventFlow } returns flowOf(
            Event(
                eventType = EventType.Delete,
                id = "12"
            )
        )
        sut.getSSEEvents()
        runTest {
            sut.state.test {
                assertEquals(expectedItem, awaitItem().get(0))
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given error event Then state should be error`() {
        val expectedItem = Event(eventType = EventType.Error)

        runTest {
            coEvery { repository.eventFlow } returns flow { throw IOException() }
            sut.state.test {
                sut.getSSEEvents()
                val item = awaitItem()
                assertEquals(sut.state.value, item)
                advanceTimeBy(1500L) // Advance the virtual time to allow state updates
                val itemAnother = awaitItem()
                assertEquals(expectedItem, itemAnother.get(0))
            }
        }
    }

}
