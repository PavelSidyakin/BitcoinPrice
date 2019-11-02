package com.example.bitcoinprice.di.screen

import com.example.bitcoinprice.di.PerFeature
import com.example.bitcoinprice.di.bitcoin_price.BitcoinPriceModule
import com.example.bitcoinprice.presentation.bitcoin_price.presenter.BitcoinPriceGraphPresenter
import dagger.Subcomponent

@Subcomponent(modules = [BitcoinPriceModule::class])
@PerFeature
interface BitcoinPriceGraphScreenComponent {

    fun getBitcoinPriceGraphPresenter(): BitcoinPriceGraphPresenter

    fun inject(a: BitcoinPriceGraphPresenter)

}