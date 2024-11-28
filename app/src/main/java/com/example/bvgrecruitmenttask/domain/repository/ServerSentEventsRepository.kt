package com.example.bvgrecruitmenttask.domain.repository

import com.example.bvgrecruitmenttask.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface ServerSentEventsRepository {
    val eventFlow: Flow<Event>
}
