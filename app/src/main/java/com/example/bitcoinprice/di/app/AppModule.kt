package com.example.bitcoinprice.di.app

import com.example.bitcoinprice.utils.rx.SchedulersProvider
import com.example.bitcoinprice.utils.rx.SchedulersProviderImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun provideSchedulersProvider(schedulersProvider: SchedulersProviderImpl) : SchedulersProvider

}