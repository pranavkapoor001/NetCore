package com.lokal.network.di

import com.lokal.network.request.defination.NetworkRequestHandler
import com.lokal.network.request.implementation.NetworkRequestHandlerImpl
import com.lokal.network.response.defination.NetworkResponseHandler
import com.lokal.network.response.implementation.NetworkResponseHandlerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object NetworkLibraryModule {

    @Provides
    fun bindNetworkRequestHandler(): NetworkRequestHandler = NetworkRequestHandlerImpl()

    @Provides
    fun bindNetworkResponseHandler(): NetworkResponseHandler = NetworkResponseHandlerImpl()

    // Not using @Binds even though we have interface -> implementation since the impl is marked internal
}