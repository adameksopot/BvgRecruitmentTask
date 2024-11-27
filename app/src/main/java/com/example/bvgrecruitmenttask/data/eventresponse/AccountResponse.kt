package com.example.bvgrecruitmenttask.data.eventresponse

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class AccountResponse(
    val username: String? = null
)