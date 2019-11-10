package com.example.bitcoinprice.presentation.bitcoin_price.view

import com.example.bitcoinprice.presentation.bitcoin_price.model.DisplayPeriod
import com.github.mikephil.charting.data.Entry
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

interface BitcoinPriceGraphView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setGraphPoints(entries: List<Entry>)

    @StateStrategyType(AddToEndStrategy::class)
    fun addDisplayPeriod(displayPeriod: DisplayPeriod)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showLoadingProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun selectDisplayPeriod(displayPeriod: DisplayPeriod)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setMaxPriceInPeriod(maxPrice: Double)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setMinPriceInPeriod(minPrice: Double)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setAveragePriceInPeriod(averagePrice: Double)

    @StateStrategyType(AddToEndStrategy::class)
    fun showGeneraError(show: Boolean)

    @StateStrategyType(AddToEndStrategy::class)
    fun showNetworkError(show: Boolean)

}