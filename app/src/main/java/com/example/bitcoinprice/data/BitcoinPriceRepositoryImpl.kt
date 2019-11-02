package com.example.bitcoinprice.data

import com.example.bitcoinprice.data.blockchain_data.BlockChainDataError
import com.example.bitcoinprice.data.blockchain_data.BlockChainDataProvider
import com.example.bitcoinprice.data.blockchain_data.model.Time
import com.example.bitcoinprice.data.blockchain_data.model.TimeUnit
import com.example.bitcoinprice.data.blockchain_data.model.json.DataPoint
import com.example.bitcoinprice.data.blockchain_data.model.json.Result
import com.example.bitcoinprice.data.blockchain_data.model.json.Status
import com.example.bitcoinprice.domain.data.BitcoinPriceRepository
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPriceRequestDataPoint
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResultCode
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResultData
import com.example.bitcoinprice.utils.logs.log
import io.reactivex.Single
import javax.inject.Inject

class BitcoinPriceRepositoryImpl
    @Inject
    constructor(private val blockChainDataProvider: BlockChainDataProvider)
    : BitcoinPriceRepository {


    override fun requestBitcoinMarketPrices(periodBeforeTodayDays: Int): Single<BitcoinPricesRequestResult> {
        return blockChainDataProvider.requestBitcoinMarketPrices(Time(periodBeforeTodayDays, TimeUnit.DAY))
            .map { convertBlockChainRequestMarketPricesResult2BitcoinPricesRequestResult(it) }
            .doOnSubscribe { log { i(TAG, "BitcoinPriceRepositoryImpl.requestBitcoinMarketPrices(): Subscribe. periodBeforeTodayDays = [${periodBeforeTodayDays}]") } }
            .doOnSuccess { log { i(TAG, "BitcoinPriceRepositoryImpl.requestBitcoinMarketPrices(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "BitcoinPriceRepositoryImpl.requestBitcoinMarketPrices(): Error", it) } }
            .onErrorResumeNext { processError(it) }
    }

    private fun processError(throwable: Throwable): Single<BitcoinPricesRequestResult> {
        return if (throwable is BlockChainDataError) {
            when(throwable.code) {
                BlockChainDataError.Code.NETWORK_ERROR -> Single.just(
                    BitcoinPricesRequestResult(
                        BitcoinPricesRequestResultCode.NETWORK_ERROR,
                        null
                    )
                )
                else -> Single.just(
                    BitcoinPricesRequestResult(
                        BitcoinPricesRequestResultCode.GENERAL_ERROR,
                        null
                    )
                )
            }
        } else {
            Single.just(
                BitcoinPricesRequestResult(
                    BitcoinPricesRequestResultCode.GENERAL_ERROR,
                    null
                )
            )
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