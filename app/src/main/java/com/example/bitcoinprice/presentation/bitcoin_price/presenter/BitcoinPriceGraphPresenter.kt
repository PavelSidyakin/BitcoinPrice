package com.example.bitcoinprice.presentation.bitcoin_price.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.bitcoinprice.TheApplication
import com.example.bitcoinprice.di.screen.BitcoinPriceGraphScreenComponent
import com.example.bitcoinprice.domain.bitcoin_price.BitcoinPriceInteractor
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPriceDataPoint
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResult
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResultCode
import com.example.bitcoinprice.presentation.bitcoin_price.view.BitcoinPriceGraphView
import com.example.bitcoinprice.utils.rx.SchedulersProvider
import com.github.mikephil.charting.data.Entry
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

@InjectViewState
class BitcoinPriceGraphPresenter

    @Inject
    constructor(
        private val bitcoinPriceInteractor: BitcoinPriceInteractor,
        private val schedulersProvider: SchedulersProvider

    ) : MvpPresenter<BitcoinPriceGraphView>() {

    private var bitcoinPriceGraphScreenComponent: BitcoinPriceGraphScreenComponent? = null

    private var currentPeriodBeforeTodayDays: Int = 0

    private var compositeDisposable = CompositeDisposable()

    init {
        bitcoinPriceGraphScreenComponent = TheApplication.getAppComponent()
            .getBitcoinPriceScreenComponent();
        bitcoinPriceGraphScreenComponent?.inject(this)

    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        currentPeriodBeforeTodayDays = 10
        requestBitcoinPricesAndDisplay()
    }

    private fun requestBitcoinPricesAndDisplay() {
        bitcoinPriceInteractor.requestBitcoinMarketPrices(currentPeriodBeforeTodayDays)
                // TODO: filter errors
            .subscribe { result -> result?.let { processRequestBitcoinPricesResult(result) } }
            .addTo(compositeDisposable)
    }

    private fun processRequestBitcoinPricesResult(result: BitcoinPricesResult) {
        if (result.resultCode == BitcoinPricesResultCode.OK) {
            result.data?.points?.let { displayBitcoinPrices(result.data.points) }
        }

    }

    private fun displayBitcoinPrices(bitcoinPricePoints: List<BitcoinPriceDataPoint>) {
        viewState.setGraphPoints(bitcoinPricePoints.map { Entry(it.timeStamp.toFloat(), it.priceUsd.toFloat()) })
    }

    override fun onDestroy() {
        super.onDestroy()
        bitcoinPriceGraphScreenComponent = null
    }
}