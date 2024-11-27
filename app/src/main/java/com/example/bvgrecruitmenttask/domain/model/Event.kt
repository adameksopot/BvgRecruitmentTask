package com.example.bvgrecruitmenttask.domain.model

import com.example.bvgrecruitmenttask.data.EventType


data class Event(
    val id: String? = null,
    val createdAt: String? = null,
    val eventType: EventType,
    val account: Account? = null
)
