package com.example.bvgrecruitmenttask.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bvgrecruitmenttask.data.EventType
import com.example.bvgrecruitmenttask.domain.model.Event
import com.example.bvgrecruitmenttask.domain.repository.ServerSentEventsRepository
import com.example.bvgrecruitmenttask.domain.time.CurrentTimeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamingViewModel
    @Inject
    constructor(
        private val serverSentEventsRepository: ServerSentEventsRepository,
        private val currentTimeProvider: CurrentTimeProvider,
    ) : ViewModel() {
        private var removalJob: Job? = null
        private var collectionJob: Job? = null
        private var lastPausedTime: Long? = null
        private var totalPauseDuration: Long = 0L

        private val searchQuery = MutableStateFlow("")

        private val _state = MutableStateFlow<List<Event>>(emptyList())
        val state: StateFlow<List<Event>> =
            searchQuery
                .combine(_state) { query, events ->
                    events.filter { event ->
                        query.isBlank() || event.account?.username?.contains(
                            query,
                            ignoreCase = true,
                        ) == true
                    }
                }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        fun onSearchQuery(newQuery: String) {
            searchQuery.value = newQuery
        }

        fun collectEvents() {
            collectionJob =
                serverSentEventsRepository
                    .eventFlow
                    .flowOn(Dispatchers.IO)
                    .onEach { event ->
                        val currentEvents = _state.value.toMutableList()
                        currentEvents.add(event)
                        _state.value = currentEvents
                    }.catch {
                        stopCoroutines()
                        val currentEvents = _state.value.toMutableList()
                        currentEvents.add(
                            Event(
                                eventType = EventType.Error,
                                timestamp = currentTimeProvider.currentTimeMillis(),
                            ),
                        )
                        _state.value = currentEvents
                    }.launchIn(viewModelScope)
                    .also { startRemovalCoroutine() }
        }

        private fun startRemovalCoroutine() {
            removalJob =
                viewModelScope.launch(Dispatchers.Default) {
                    while (isActive) {
                        delay(1000) // The deletion will be done every second
                        val currentTime = System.currentTimeMillis()
                        val adjustedTime =
                            currentTime - totalPauseDuration // Adjust current time based on pause duration

                        _state.update { events ->
                            val filteredEvents =
                                events.filter { event ->
                                    val eventAge = adjustedTime - event.timestamp
                                    val isExpired = eventAge >= ITEM_LIFESPAN
                                    Log.d(
                                        TAG,
                                        "Filtering event: timestamp=${event.timestamp}, eventAge=$eventAge, isExpired=$isExpired",
                                    )
                                    !isExpired
                                }

                            // Log the filtered list to see which events remain
                            Log.d(
                                TAG,
                                "Filtered events: ${filteredEvents.size}, Remaining events: $filteredEvents",
                            )
                            filteredEvents
                        }
                    }
                }
        }

        private fun stopCoroutines() {
            removalJob?.cancel()
            collectionJob?.cancel()
            lastPausedTime = System.currentTimeMillis()
        }
    }

const val ITEM_LIFESPAN =
    1000L // the deletion stops on internet disconnection but as long as the internet is connected, the deletion continues
const val TAG = "ServerSentEventsViewModel"
