package com.example.bvgrecruitmenttask


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bvgrecruitmenttask.data.EventType
import com.example.bvgrecruitmenttask.domain.model.Event
import com.example.bvgrecruitmenttask.domain.repository.ServerSentEventsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ServerSentEventsViewModel @Inject constructor(
    private val serverSentEventsRepository: ServerSentEventsRepository,
) : ViewModel() {


    private var _state: MutableStateFlow<List<Event>> = MutableStateFlow(emptyList())
    val state: StateFlow<List<Event>> = _state


     fun getSSEEvents() =
        serverSentEventsRepository.eventFlow.flowOn(Dispatchers.IO)
            .onEach { event ->
                val currentEvents = _state.value.toMutableList()
                currentEvents.add(event)
                _state.value = currentEvents
            }
            .catch {
                val currentEvents = _state.value.toMutableList()
                currentEvents.add(Event(eventType = EventType.Error))
                _state.value = currentEvents
            }
            .launchIn(viewModelScope)
}