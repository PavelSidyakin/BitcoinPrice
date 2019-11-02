package com.example.bitcoinprice.di.screen

import com.example.bitcoinprice.MainActivity
import com.example.bitcoinprice.di.PerFeature
import com.example.bitcoinprice.di.bitcoin_price.BitcoinPriceModule
import dagger.Subcomponent

@Subcomponent(modules = [BitcoinPriceModule::class])
@PerFeature
interface BitcoinPriceScreenComponent {

    fun inject(a: MainActivity)

}