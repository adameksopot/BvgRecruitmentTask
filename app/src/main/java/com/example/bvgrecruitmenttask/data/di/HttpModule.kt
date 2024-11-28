package com.example.bvgrecruitmenttask.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class HttpModule {
    @Provides
    fun provideSseOkHttpClient() =
        OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build()

    @Provides
    fun provideRequest() =
        Request.Builder()
            .url("https://mas.to/api/v1/streaming/public")
            .header("Accept", "application/json; q=0.5")
            .addHeader("Accept", "text/event-stream")
            .addHeader("Authorization", "Bearer e5CqekqbA2PhDxAcQhoJBpFhvNqSKvshXXpGi4B8g7M")
            .build()
}
