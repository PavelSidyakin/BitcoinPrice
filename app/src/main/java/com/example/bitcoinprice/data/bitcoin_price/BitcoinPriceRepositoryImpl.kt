package com.example.bitcoinprice.data.bitcoin_price

import com.example.bitcoinprice.data.bitcoin_price.blockchain_data.BlockChainDataError
import com.example.bitcoinprice.data.bitcoin_price.blockchain_data.BlockChainDataProvider
import com.example.bitcoinprice.data.bitcoin_price.blockchain_data.model.Time
import com.example.bitcoinprice.data.bitcoin_price.blockchain_data.model.TimeUnit
import com.example.bitcoinprice.data.bitcoin_price.blockchain_data.model.json.DataPoint
import com.example.bitcoinprice.data.bitcoin_price.blockchain_data.model.json.Result
import com.example.bitcoinprice.data.bitcoin_price.blockchain_data.model.json.Status
import com.example.bitcoinprice.domain.bitcoin_price.data.BitcoinPriceRepository
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPriceRequestDataPoint
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResultCode
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResultData
import com.example.bitcoinprice.model.domain.TimePeriod
import com.example.bitcoinprice.model.domain.TimePeriodUnit
import com.example.bitcoinprice.utils.logs.log
import io.reactivex.Single
import javax.inject.Inject

class BitcoinPriceRepositoryImpl
    @Inject
    constructor(private val blockChainDataProvider: BlockChainDataProvider)
    : BitcoinPriceRepository {


    override fun requestBitcoinMarketPrices(periodBeforeToday: TimePeriod): Single<BitcoinPricesRequestResult> {
        return blockChainDataProvider.requestBitcoinMarketPrices(convertTimePeriod2Time(periodBeforeToday))
            .map { convertBlockChainRequestMarketPricesResult2BitcoinPricesRequestResult(it) }
            .doOnSubscribe { log { i(TAG, "BitcoinPriceRepositoryImpl.requestBitcoinMarketPrices(): Subscribe. periodBeforeTodayDays = [${periodBeforeToday}]") } }
            .doOnSuccess { log { i(TAG, "BitcoinPriceRepositoryImpl.requestBitcoinMarketPrices(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "BitcoinPriceRepositoryImpl.requestBitcoinMarketPrices(): Error", it) } }
            .onErrorResumeNext { processError(it) }
    }

    private fun processError(throwable: Throwable): Single<BitcoinPricesRequestResult> {
        return if (throwable is BlockChainDataError) {
            when(throwable.code) {
                BlockChainDataError.Code.NETWORK_ERROR -> Single.just(
                    BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.NETWORK_ERROR, null))
                else -> Single.just(
                    BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.GENERAL_ERROR, null))
            }
        } else {
            Single.just(
                BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.GENERAL_ERROR, null))
        }
    }

    private fun convertTimePeriod2Time(timePeriod: TimePeriod): Time {
        return when(timePeriod.timePeriodUnit) {
            TimePeriodUnit.DAY -> Time(timePeriod.count, TimeUnit.DAY)
            TimePeriodUnit.MONTH -> Time(timePeriod.count * 4, TimeUnit.WEEK)
            TimePeriodUnit.YEAR -> Time(timePeriod.count, TimeUnit.YEAR)
            TimePeriodUnit.ALL -> Time(0, TimeUnit.ALL)
        }
    }

    private fun convertBlockChainRequestMarketPricesResult2BitcoinPricesRequestResult(
        blockChainResult: Result
    ): BitcoinPricesRequestResult {

        return BitcoinPricesRequestResult(
            convertStatus2BitcoinPricesRequestResultCode(blockChainResult.status),
            BitcoinPricesRequestResultData(
                blockChainResult.values?.map { convertDataPoint2BitcoinPriceDataPoint(it) })
        )
    }

    private fun convertDataPoint2BitcoinPriceDataPoint(dataPoint: DataPoint): BitcoinPriceRequestDataPoint {
        return BitcoinPriceRequestDataPoint(
            dataPoint.x,
            dataPoint.y
        )
    }

    private fun convertStatus2BitcoinPricesRequestResultCode(status: Status): BitcoinPricesRequestResultCode {
        return when(status) {
            Status.ok -> BitcoinPricesRequestResultCode.OK
            else -> BitcoinPricesRequestResultCode.GENERAL_ERROR
        }
    }

    companion object {
        private const val TAG = "BitcoinPriceRepository"
    }

}