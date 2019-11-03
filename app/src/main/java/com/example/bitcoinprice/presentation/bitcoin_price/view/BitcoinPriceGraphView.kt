package com.example.bitcoinprice.presentation.bitcoin_price.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.bitcoinprice.presentation.bitcoin_price.model.DisplayPeriod
import com.github.mikephil.charting.data.Entry

interface BitcoinPriceGraphView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setGraphPoints(entries: List<Entry>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun addDisplayPeriod(displayPeriod: DisplayPeriod)

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun showLoadingProgress(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun selectDisplayPeriod(displayPeriod: DisplayPeriod)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setMaxPriceInPeriod(maxPrice: Double)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setMinPriceInPeriod(minPrice: Double)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setAveragePriceInPeriod(averagePrice: Double)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showGeneraError(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showNetworkError(show: Boolean)

}