package com.example.bvgrecruitmenttask.data.eventresponse

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerSentEventResponse(
    val id: String,
    @Json(name = "created_at") val createdAt: String,
    val account: AccountResponse? = null,
)
