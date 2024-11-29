package com.example.bvgrecruitmenttask.data.time

import com.example.bvgrecruitmenttask.domain.time.CurrentTimeProvider
import javax.inject.Inject

class CurrentTimeProviderImpl
    @Inject
    constructor() : CurrentTimeProvider {
        override fun currentTimeMillis(): Long = System.currentTimeMillis()
    }
