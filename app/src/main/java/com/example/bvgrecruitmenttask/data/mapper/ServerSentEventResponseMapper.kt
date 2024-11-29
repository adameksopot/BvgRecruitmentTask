package com.example.bvgrecruitmenttask.data.mapper

import com.example.bvgrecruitmenttask.data.EventType
import com.example.bvgrecruitmenttask.data.eventresponse.ServerSentEventResponse
import com.example.bvgrecruitmenttask.domain.model.Account
import com.example.bvgrecruitmenttask.domain.model.Event
import com.example.bvgrecruitmenttask.domain.time.CurrentTimeProvider
import com.squareup.moshi.JsonAdapter
import javax.inject.Inject

class ServerSentEventResponseMapper
    @Inject
    constructor(
        private val adapter: JsonAdapter<ServerSentEventResponse>,
        private val currentTimeProvider: CurrentTimeProvider,
    ) {
        fun mapEventResponse(
            json: String,
            type: String?,
        ): Event? =
            try {
                val eventResponse = adapter.fromJson(json)

                eventResponse?.let {
                    Event(
                        id = it.id,
                        createdAt = it.createdAt,
                        eventType = determineEventType(type),
                        account = Account(it.account?.username),
                        timestamp = currentTimeProvider.currentTimeMillis(),
                    )
                }
            } catch (e: Exception) {
                null
            }

        private fun determineEventType(type: String?): EventType =
            when (type) {
                "update" -> EventType.Update
                else -> EventType.Unknown
            }
    }
