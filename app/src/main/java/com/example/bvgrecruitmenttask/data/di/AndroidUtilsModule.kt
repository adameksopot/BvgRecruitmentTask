package com.example.bvgrecruitmenttask.data.di

import com.example.bvgrecruitmenttask.data.eventresponse.ServerSentEventResponse
import com.example.bvgrecruitmenttask.data.time.CurrentTimeProviderImpl
import com.example.bvgrecruitmenttask.domain.time.CurrentTimeProvider
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AndroidUtilsModule {

    companion object {
        @Provides
        fun provideMoshi(): Moshi = Moshi.Builder().build()

        @Provides
        fun provideServerSentEventResponseAdapter(moshi: Moshi) = moshi.adapter(ServerSentEventResponse::class.java)
    }

    @Binds
   abstract fun bindCurrentTimeProvider(currentTimeProviderImpl: CurrentTimeProviderImpl): CurrentTimeProvider
}
