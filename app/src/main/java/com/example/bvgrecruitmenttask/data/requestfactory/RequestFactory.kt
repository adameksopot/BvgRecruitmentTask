package com.example.bvgrecruitmenttask.data.requestfactory

import okhttp3.Request
import javax.inject.Inject

class RequestFactory
    @Inject
    constructor() {
        fun createSseRequest(): Request =
            Request
                .Builder()
                .url("https://mas.to/api/v1/streaming/public")
                .header("Accept", "application/json; q=0.5")
                .addHeader("Accept", "text/event-stream")
                .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
                .build()
    }

const val ACCESS_TOKEN = "e5CqekqbA2PhDxAcQhoJBpFhvNqSKvshXXpGi4B8g7M"
