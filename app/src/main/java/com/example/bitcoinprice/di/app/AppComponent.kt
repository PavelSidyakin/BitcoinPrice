package com.example.bitcoinprice.di.app

import com.example.bitcoinprice.TheApplication
import com.example.bitcoinprice.di.screen.BitcoinPriceGraphScreenComponent
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(mapsActivity: TheApplication)

    fun getBitcoinPriceScreenComponent(): BitcoinPriceGraphScreenComponent

    interface Builder {
        fun build(): AppComponent
    }
}