package com.example.bvgrecruitmenttask.data.di

import com.example.bvgrecruitmenttask.data.repository.ServerSentEventsRepositoryEventsRepositoryImpl
import com.example.bvgrecruitmenttask.domain.repository.ServerSentEventsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(impl: ServerSentEventsRepositoryEventsRepositoryImpl): ServerSentEventsRepository
}
