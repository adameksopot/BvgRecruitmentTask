package com.example.bvgrecruitmenttask.data.repository

import com.example.bvgrecruitmenttask.data.EventType
import com.example.bvgrecruitmenttask.data.mapper.ServerSentEventResponseMapper
import com.example.bvgrecruitmenttask.data.requestfactory.RequestFactory
import com.example.bvgrecruitmenttask.domain.model.Event
import com.example.bvgrecruitmenttask.domain.repository.ServerSentEventsRepository
import com.example.bvgrecruitmenttask.domain.time.CurrentTimeProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import javax.inject.Inject

class ServerSentEventsRepositoryEventsRepositoryImpl
    @Inject
    constructor(
        private val client: OkHttpClient,
        private val requestFactory: RequestFactory,
        private val mapper: ServerSentEventResponseMapper,
        private val currentTimeProvider: CurrentTimeProvider,
    ) : ServerSentEventsRepository {
        override val eventFlow: Flow<Event> =
            callbackFlow {
                val eventSource =
                    EventSources.createFactory(client).newEventSource(
                        requestFactory.createSseRequest(),
                        object : EventSourceListener() {
                            override fun onOpen(
                                eventSource: EventSource,
                                response: Response,
                            ) {
                            }

                            override fun onEvent(
                                eventSource: EventSource,
                                id: String?,
                                type: String?,
                                data: String,
                            ) {
                                val result =
                                    when (type) {
                                        "delete" -> handleDeleteEvent(data)
                                        else -> mapper.mapEventResponse(json = data, type = type)
                                    }

                                result?.let { trySend(it) }
                            }

                            override fun onClosed(eventSource: EventSource) {
                                close()
                            }

                            override fun onFailure(
                                eventSource: EventSource,
                                t: Throwable?,
                                response: Response?,
                            ) {
                                t?.let { close(it) }
                            }
                        },
                    )

                awaitClose {
                    eventSource.cancel()
                }
            }

        private fun handleDeleteEvent(data: String): Event =
            Event(
                id = data,
                eventType = EventType.Delete,
                timestamp = currentTimeProvider.currentTimeMillis(),
            )
    }
