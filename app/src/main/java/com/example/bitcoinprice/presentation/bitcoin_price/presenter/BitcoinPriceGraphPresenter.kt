package com.example.bitcoinprice.presentation.bitcoin_price.presenter

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
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class BitcoinPriceGraphPresenter
@Inject
constructor(
    private val bitcoinPriceInteractor: BitcoinPriceInteractor,
    private val schedulersProvider: SchedulersProvider//,
    //private var bitcoinPriceGraphScreenComponent: BitcoinPriceGraphScreenComponent? = getBitcoinPriceGraphScreenComponent()
) : MvpPresenter<BitcoinPriceGraphView>() {

    private var currentDisplayPeriod: DisplayPeriod = DisplayPeriod.DAY_3

    private var requestPricesDisposable: Disposable? = null

    override fun onFirstViewAttach() {
        DisplayPeriod.values().forEach { viewState.addDisplayPeriod(it) }
        viewState.selectDisplayPeriod(currentDisplayPeriod)
    }

    fun onDisplayPeriodSelected(displayPeriod: DisplayPeriod) {
        log { i(TAG, "BitcoinPriceGraphPresenter.onDisplayPeriodSelected(). displayPeriod = [${displayPeriod}]") }

        if (requestPricesDisposable?.isDisposed == false) {
            requestPricesDisposable?.dispose()
        }

        currentDisplayPeriod = displayPeriod
        requestBitcoinPricesAndDisplay()
        viewState.selectDisplayPeriod(currentDisplayPeriod)
    }

    private fun requestBitcoinPricesAndDisplay() {

        requestPricesDisposable = hideAllErrors()
            .andThen(Single.defer { bitcoinPriceInteractor.requestBitcoinMarketPrices(currentDisplayPeriod.timePeriod) })
            .doOnSubscribe { log { i(TAG, "BitcoinPriceGraphPresenter.requestBitcoinPricesAndDisplay(): Subscribe. ") } }
            .doOnSuccess { log { i(TAG, "BitcoinPriceGraphPresenter.requestBitcoinPricesAndDisplay(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "BitcoinPriceGraphPresenter.requestBitcoinPricesAndDisplay(): Error", it) } }
            .flatMapCompletable { result -> processRequestBitcoinPricesResult(result) }
            .doOnSubscribe { showLoadingProgress(true) }
            .doFinally { showLoadingProgress(false) }
            .subscribeOn(schedulersProvider.io())
            .doOnDispose { requestPricesDisposable = null }
            .subscribe({ }, { })
    }

    fun retry() {
        requestBitcoinPricesAndDisplay()
    }

    private fun processRequestBitcoinPricesResult(result: BitcoinPricesResult): Completable {
        return when (result.resultCode) {
            BitcoinPricesResultCode.OK -> processSuccessResult(result)
            BitcoinPricesResultCode.NETWORK_ERROR ->
                Completable.fromCallable { viewState.showNetworkError(true) }
                    .subscribeOn(schedulersProvider.main())
            BitcoinPricesResultCode.GENERAL_ERROR ->
                Completable.fromCallable { viewState.showGeneraError(true) }
                    .subscribeOn(schedulersProvider.main())
        }

    }

    private fun processSuccessResult(result: BitcoinPricesResult): Completable {
        return hideAllErrors()
            .andThen(Completable.defer { result.data?.points?.let { processBitcoinPrices(result.data.points) } })
    }

    private fun hideAllErrors(): Completable {
        return Completable.fromCallable {
            viewState.showGeneraError(false)
            viewState.showNetworkError(false)
        }
            .subscribeOn(schedulersProvider.main())
    }

    private fun showLoadingProgress(show: Boolean) {
        runOnMainThread { viewState.showLoadingProgress(show) }
    }

    private fun processBitcoinPrices(bitcoinPricePoints: List<BitcoinPriceDataPoint>): Completable {
        return displayGraph(bitcoinPricePoints)
            .andThen(Completable.defer { calcAndDisplayInfo(bitcoinPricePoints) })
    }

    private fun calcAndDisplayInfo(bitcoinPricePoints: List<BitcoinPriceDataPoint>): Completable {
        return Single.fromCallable { bitcoinPricePoints.map { it.priceUsd } }
            .map { pricesList -> InfoData(pricesList.max(), pricesList.min(), pricesList.average()) }
            .flatMapCompletable { infoData -> displayInfoData(infoData) }
            .subscribeOn(schedulersProvider.computation())
    }

    private fun displayInfoData(infoData: InfoData): Completable {
        return Completable.fromCallable {
            infoData.maxPrice?.let { viewState.setMaxPriceInPeriod(it) }
            infoData.minPrice?.let { viewState.setMinPriceInPeriod(it) }
            infoData.averagePrice?.let { viewState.setAveragePriceInPeriod(it) }
        }.subscribeOn(schedulersProvider.main())
    }

    private fun displayGraph(bitcoinPricePoints: List<BitcoinPriceDataPoint>): Completable {
        return Completable.fromCallable {
            viewState.setGraphPoints(bitcoinPricePoints.map { dataPoint ->
                Entry(
                    dataPoint.timeStamp.toFloat(),
                    dataPoint.priceUsd.toFloat(),
                    dataPoint
                )
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (requestPricesDisposable?.isDisposed == false) {
            requestPricesDisposable?.dispose()
            requestPricesDisposable = null
        }
        //bitcoinPriceGraphScreenComponent = null
    }

    private fun runOnMainThread(block: () -> Unit) {
        Single.fromCallable { block() }
            .subscribeOn(schedulersProvider.main())
            .subscribe()
    }

    private data class InfoData(
        val maxPrice: Double?,
        val minPrice: Double?,
        val averagePrice: Double?
    )

    private companion object {
        private const val TAG = "BitcoinPriceGraphPr"

        private fun getBitcoinPriceGraphScreenComponent(): BitcoinPriceGraphScreenComponent {
            return TheApplication.getAppComponent()
                .getBitcoinPriceScreenComponent()
        }

    }
}