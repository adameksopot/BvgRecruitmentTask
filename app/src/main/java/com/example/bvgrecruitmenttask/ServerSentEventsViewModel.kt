package com.example.bvgrecruitmenttask


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bvgrecruitmenttask.data.EventType
import com.example.bvgrecruitmenttask.domain.model.Event
import com.example.bvgrecruitmenttask.domain.repository.ServerSentEventsRepository
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ServerSentEventsViewModel @Inject constructor(
    private val serverSentEventsRepository: ServerSentEventsRepository
) : ViewModel() {

    private var removalJob: Job? = null
    private var collectionJob: Job? = null

    private val _searchQuery = MutableStateFlow("")

    private val _state = MutableStateFlow<List<Event>>(emptyList())
    val state: StateFlow<List<Event>> = _searchQuery.combine(_state) { query, events ->
        events.filter { event ->
            query.isBlank() || event.account?.username?.contains(query, ignoreCase = true) == true
        }
    }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun collectEvents() {
        collectionJob = serverSentEventsRepository.eventFlow
            .flowOn(Dispatchers.IO)
            .onEach { event ->
                val currentEvents = _state.value.toMutableList()
                currentEvents.add(event)
                _state.value = currentEvents
            }
            .catch { e ->
                stopCoroutines()
                val currentEvents = _state.value.toMutableList()
                currentEvents.add(Event(eventType = EventType.Error))
                _state.value = currentEvents
            }
            .launchIn(viewModelScope).also {
             startRemovalCoroutine()
            }
    }

    fun onSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun startRemovalCoroutine() {
        removalJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(100)
                val currentEvents = _state.value.toMutableList()
                val currentTime = System.currentTimeMillis()

                currentEvents.removeAll { event ->
                    currentTime - event.timestamp >= ITEM_LIFESPAN
                }
                _state.value = currentEvents
            }
        }
    }

    private fun stopCoroutines() {
        removalJob?.cancel()
        collectionJob?.cancel()

    }
}
const val ITEM_LIFESPAN = 10000L // the deletion stops on internet disconnection but as long as the internet is connected, the deletion continues
const val TAG = "ServerSentEventsViewModel"