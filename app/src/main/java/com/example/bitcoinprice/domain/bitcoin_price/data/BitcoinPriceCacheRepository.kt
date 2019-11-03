package com.example.bitcoinprice.domain.bitcoin_price.data

import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import com.example.bitcoinprice.model.domain.TimePeriod
import io.reactivex.Completable
import io.reactivex.Single
import java.util.Optional

interface BitcoinPriceCacheRepository {

    fun findCachedBitcoinMarketPrices(periodBeforeToday: TimePeriod): Single<Optional<BitcoinPricesRequestResult>>

    fun putBitcoinMarketPrices(periodBeforeToday: TimePeriod, result: BitcoinPricesRequestResult): Completable

}