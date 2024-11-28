package com.example.bvgrecruitmenttask.data.mapper

import com.example.bvgrecruitmenttask.data.EventType
import com.example.bvgrecruitmenttask.data.eventresponse.ServerSentEventResponse
import com.example.bvgrecruitmenttask.domain.model.Account
import com.example.bvgrecruitmenttask.domain.model.Event
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import javax.inject.Inject

class ServerSentEventResponseMapper
    @Inject
    constructor(moshi: Moshi) {
        private val serverSentEventResponseAdapter: JsonAdapter<ServerSentEventResponse> =
            moshi.adapter(
                ServerSentEventResponse::class.java,
            )

        fun mapEventResponse(
            json: String,
            type: String?,
        ): Event? {
            return try {
                val eventResponse = serverSentEventResponseAdapter.fromJson(json)

                eventResponse?.let {
                    Event(
                        id = it.id,
                        createdAt = it.createdAt,
                        eventType = determineEventType(type),
                        account = Account(it.account?.username),
                    )
                }
            } catch (e: Exception) {
                null
            }
        }

        private fun determineEventType(type: String?): EventType {
            return when (type) {
                "update" -> EventType.Update
                else -> EventType.Unknown
            }
        }
    }
