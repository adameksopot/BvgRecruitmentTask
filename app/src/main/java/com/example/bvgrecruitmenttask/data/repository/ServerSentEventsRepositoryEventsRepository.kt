package com.example.bvgrecruitmenttask.data.repository

import android.util.Log
import com.example.bvgrecruitmenttask.data.EventType
import com.example.bvgrecruitmenttask.data.mapper.ServerSentEventResponseMapper
import com.example.bvgrecruitmenttask.domain.model.Event
import com.example.bvgrecruitmenttask.domain.repository.ServerSentEventsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import javax.inject.Inject

class ServerSentEventsRepositoryEventsRepositoryImpl @Inject constructor(
    private val client: OkHttpClient,
    private val request: Request,
    private val mapper: ServerSentEventResponseMapper
) : ServerSentEventsRepository {
    override val eventFlow: Flow<Event> = callbackFlow {
        val eventSource = EventSources.createFactory(client).newEventSource(request, object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d(TAG, "Connection Opened")
            }

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                try {
                    val result = when (type) {
                        "delete" -> handleDeleteEvent(data)
                        else -> mapper.mapEventResponse(json = data, type = type)
                    }

                    result?.let { trySend(it) }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing event: ${e.message}", e)
                }
            }

            override fun onClosed(eventSource: EventSource) {
                Log.d(TAG, "Connection Closed")
                close()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                Log.e(TAG, "Connection Failed: ${t?.message} - Response: ${response?.body}")
                t?.let { close(it) }
            }
        })

        awaitClose {
            eventSource.cancel()
        }
    }
    private fun handleDeleteEvent(data: String): Event = Event(
                id = data,
                eventType = EventType.Delete,
            )
}

const val TAG = "EventsRepositoryImpl"
