package com.example.bitcoinprice.presentation.bitcoin_price.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.github.mikephil.charting.data.Entry

interface BitcoinPriceGraphView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setGraphPoints(entries: List<Entry>)
}