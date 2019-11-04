package com.example.bitcoinprice.domain.bitcoin_price

import com.example.bitcoinprice.domain.bitcoin_price.data.BitcoinPriceCacheRepository
import com.example.bitcoinprice.domain.bitcoin_price.data.BitcoinPriceRepository
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResultCode
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResultData
import com.example.bitcoinprice.model.domain.TimePeriod
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPriceDataPoint
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResult
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResultCode
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResultData
import com.example.bitcoinprice.utils.logs.log
import com.example.bitcoinprice.utils.rx.SchedulersProvider
import io.reactivex.Single
import javax.inject.Inject

class BitcoinPriceInteractorImpl
@Inject
constructor(
    private val bitcoinPriceCacheRepository: BitcoinPriceCacheRepository,
    private val bitcoinPriceRepository: BitcoinPriceRepository,
    private val schedulersProvider: SchedulersProvider
) : BitcoinPriceInteractor {

    override fun requestBitcoinMarketPrices(periodBeforeToday: TimePeriod): Single<BitcoinPricesResult> {
        return bitcoinPriceCacheRepository.findCachedBitcoinMarketPrices(periodBeforeToday)
            .flatMap { optionalResult ->
                if (optionalResult.isPresent) {
                    Single.just(optionalResult.get())
                } else {
                    performRealRequestAndPutInCache(periodBeforeToday)
                }
            }
            .map { requestResult -> convertBitcoinPricesRequestResult2BitcoinPricesResult(requestResult) }
            .subscribeOn(schedulersProvider.io())
            .doOnSubscribe { log { i(TAG, "BitcoinPriceInteractorImpl.requestBitcoinMarketPrices(): Subscribe. periodBeforeTodayDays = [${periodBeforeToday}]") } }
            .doOnSuccess { log { i(TAG, "BitcoinPriceInteractorImpl.requestBitcoinMarketPrices(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "BitcoinPriceInteractorImpl.requestBitcoinMarketPrices(): Error", it) } }

    }

    private fun performRealRequestAndPutInCache(periodBeforeToday: TimePeriod): Single<BitcoinPricesRequestResult> {
        return bitcoinPriceRepository.requestBitcoinMarketPrices(periodBeforeToday)
            .flatMap { result ->
                if (result.resultCode == BitcoinPricesRequestResultCode.OK) {
                    if (result.data != null) {
                        bitcoinPriceCacheRepository.putBitcoinMarketPrices(periodBeforeToday, result)
                            .toSingleDefault(result)
                    } else {
                        Single.just(BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.GENERAL_ERROR, null))
                    }
                } else {
                    Single.just(result)
                }
            }
    }

    private fun convertBitcoinPricesRequestResult2BitcoinPricesResult(requestResult: BitcoinPricesRequestResult): BitcoinPricesResult {
        return BitcoinPricesResult(
            convertBitcoinPricesRequestResultCode2BitcoinPricesResultCode(requestResult.resultCode),
            convertBitcoinPricesRequestResultData2BitcoinPricesResultData(requestResult.data)
        )
    }

    private fun convertBitcoinPricesRequestResultCode2BitcoinPricesResultCode(requestResultCode: BitcoinPricesRequestResultCode): BitcoinPricesResultCode {
        return when (requestResultCode) {
            BitcoinPricesRequestResultCode.OK -> BitcoinPricesResultCode.OK
            BitcoinPricesRequestResultCode.NETWORK_ERROR -> BitcoinPricesResultCode.NETWORK_ERROR
            BitcoinPricesRequestResultCode.GENERAL_ERROR -> BitcoinPricesResultCode.GENERAL_ERROR
        }

    }

    private fun convertBitcoinPricesRequestResultData2BitcoinPricesResultData(requestResultData: BitcoinPricesRequestResultData?): BitcoinPricesResultData? {
        return requestResultData?.let { BitcoinPricesResultData(requestResultData.points?.map { BitcoinPriceDataPoint(it.timeStamp, it.priceUsd) }) }
    }

    private companion object {
        private const val TAG = "BitcoinPriceInteractor"
    }
}