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
            .doOnSubscribe { showLoadingProgress(true) }
            .doOnSubscribe { hideAllErrors() }
            .doFinally { showLoadingProgress(false) }
            .doOnSuccess { result -> result?.let { processRequestBitcoinPricesResult(result) } }
            .subscribeOn(schedulersProvider.io())
            .doOnSubscribe { log { i(TAG, "BitcoinPriceGraphPresenter.requestBitcoinPricesAndDisplay(): Subscribe. ") } }
            .doOnSuccess { log { i(TAG, "BitcoinPriceGraphPresenter.requestBitcoinPricesAndDisplay(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "BitcoinPriceGraphPresenter.requestBitcoinPricesAndDisplay(): Error", it) } }
            .subscribe()
            .addTo(compositeDisposable)
    }

    fun retry() {
        requestBitcoinPricesAndDisplay()
    }

    private fun processRequestBitcoinPricesResult(result: BitcoinPricesResult) {
        when (result.resultCode) {
            BitcoinPricesResultCode.OK -> processSuccessResult(result)
            BitcoinPricesResultCode.NETWORK_ERROR -> runOnMainThread { viewState.showNetworkError(true) }
            BitcoinPricesResultCode.GENERAL_ERROR -> runOnMainThread { viewState.showGeneraError(true) }
        }
    }

    private fun processSuccessResult(result: BitcoinPricesResult) {
        hideAllErrors()
        result.data?.points?.let { processBitcoinPrices(result.data.points) }
    }

    private fun hideAllErrors() {
        runOnMainThread {
            viewState.showGeneraError(false)
            viewState.showNetworkError(false)
        }
    }

    private fun showLoadingProgress(show: Boolean) {
        runOnMainThread { viewState.showLoadingProgress(show)  }
    }

    private fun processBitcoinPrices(bitcoinPricePoints: List<BitcoinPriceDataPoint>) {
        viewState.setGraphPoints(bitcoinPricePoints.map { dataPoint -> Entry(
            dataPoint.timeStamp.toFloat(),
            dataPoint.priceUsd.toFloat(),
            dataPoint)
        })

        calcAndDisplayInfo(bitcoinPricePoints)
    }

    private fun calcAndDisplayInfo(bitcoinPricePoints: List<BitcoinPriceDataPoint>) {
        Single.fromCallable { bitcoinPricePoints.map { it.priceUsd } }
            .map { pricesList -> InfoData(pricesList.max(), pricesList.min(), pricesList.average()) }
            .observeOn(schedulersProvider.main())
            .map { infoData -> displayInfoData(infoData) }
            .subscribeOn(schedulersProvider.computation())
            .subscribe()
            .addTo(compositeDisposable)
    }

    private fun displayInfoData(infoData: InfoData) {
        infoData.maxPrice?.let { viewState.setMaxPriceInPeriod(it) }
        infoData.minPrice?.let { viewState.setMinPriceInPeriod(it) }
        infoData.averagePrice?.let { viewState.setAveragePriceInPeriod(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        bitcoinPriceGraphScreenComponent = null
    }

    private fun runOnMainThread(block: () -> Unit) {
        Single.fromCallable { block() }
            .subscribeOn(schedulersProvider.main())
            .subscribe()
            .addTo(compositeDisposable)
    }

    data class InfoData (
        val maxPrice: Double?,
        val minPrice: Double?,
        val averagePrice: Double?
    )

    companion object {
        private const val TAG = "BitcoinPriceGraphPr"
    }
}