package com.example.bitcoinprice.domain.bitcoin_price.data

import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import io.reactivex.Completable
import io.reactivex.Single
import java.util.Optional

interface BitcoinPriceCacheRepository {

    fun findCachedBitcoinMarketPrices(periodBeforeTodayDays: Int): Single<Optional<BitcoinPricesRequestResult>>

    fun putBitcoinMarketPrices(periodBeforeTodayDays: Int, result: BitcoinPricesRequestResult): Completable

}