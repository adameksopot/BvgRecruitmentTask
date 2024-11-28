package com.example.bvgrecruitmenttask.domain.time

fun interface CurrentTimeProvider {
    fun currentTimeMillis(): Long
}
