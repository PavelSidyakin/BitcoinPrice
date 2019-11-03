package com.example.bitcoinprice.presentation.bitcoin_price.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.bitcoinprice.TheApplication
import com.example.bitcoinprice.di.screen.BitcoinPriceGraphScreenComponent
import com.example.bitcoinprice.domain.bitcoin_price.BitcoinPriceInteractor
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPriceDataPoint
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResult
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResultCode
import com.example.bitcoinprice.presentation.bitcoin_price.model.DisplayPeriod
import com.example.bitcoinprice.presentation.bitcoin_price.view.BitcoinPriceGraphView
import com.example.bitcoinprice.utils.logs.log
import com.example.bitcoinprice.utils.rx.SchedulersProvider
import com.github.mikephil.charting.data.Entry
import io.reactivex.Single
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

    private var currentDisplayPeriod: DisplayPeriod = DisplayPeriod.DAY_3

    private var compositeDisposable = CompositeDisposable()

    init {
        bitcoinPriceGraphScreenComponent = TheApplication.getAppComponent()
            .getBitcoinPriceScreenComponent();
        bitcoinPriceGraphScreenComponent?.inject(this)

    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        DisplayPeriod.values().forEach { viewState.addDisplayPeriod(it) }
        viewState.selectDisplayPeriod(currentDisplayPeriod)
    }

    fun onDisplayPeriodSelected(displayPeriod: DisplayPeriod) {
        log { i(TAG, "BitcoinPriceGraphPresenter.onDisplayPeriodSelected(). displayPeriod = [${displayPeriod}]") }

        currentDisplayPeriod = displayPeriod
        requestBitcoinPricesAndDisplay()
    }

    private fun requestBitcoinPricesAndDisplay() {
        bitcoinPriceInteractor.requestBitcoinMarketPrices(currentDisplayPeriod.timePeriod)
                // TODO: filter errors
            .doOnSubscribe { showLoadingProgress(true) }
            .doFinally { showLoadingProgress(false) }
            .subscribe { result -> result?.let { processRequestBitcoinPricesResult(result) } }
            .addTo(compositeDisposable)
    }

    private fun processRequestBitcoinPricesResult(result: BitcoinPricesResult) {
        if (result.resultCode == BitcoinPricesResultCode.OK) {
            result.data?.points?.let { displayBitcoinPrices(result.data.points) }
        }

    }

    private fun showLoadingProgress(show: Boolean) {
        Single.fromCallable { viewState.showLoadingProgress(show) }
            .subscribeOn(schedulersProvider.main())
            .subscribe()
            .addTo(compositeDisposable)
    }

    private fun displayBitcoinPrices(bitcoinPricePoints: List<BitcoinPriceDataPoint>) {
        viewState.setGraphPoints(bitcoinPricePoints.map { dataPoint -> Entry(
            dataPoint.timeStamp.toFloat(),
            dataPoint.priceUsd.toFloat(),
            dataPoint)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        bitcoinPriceGraphScreenComponent = null
    }

    companion object {
        private const val TAG = "BitcoinPriceGraphPr"
    }
}