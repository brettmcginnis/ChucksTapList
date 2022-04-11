package com.serge.chuckstaplist.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
object ChucksApiModule {

    @Provides
    fun provideChucksApi(retrofit: Retrofit): ChucksApi = retrofit.create(ChucksApi::class.java)
}
