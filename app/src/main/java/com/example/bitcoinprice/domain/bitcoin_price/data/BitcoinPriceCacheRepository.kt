package com.example.bitcoinprice.domain.bitcoin_price.data

import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import com.example.bitcoinprice.model.domain.TimePeriod
import io.reactivex.Completable
import io.reactivex.Single
import java.util.Optional

interface BitcoinPriceCacheRepository {

    /**
     * Searches [BitcoinPricesRequestResult] in a cache.
     *
     * @param periodBeforeToday Requested period.
     *
     * @return Single with Optional request result. If result is not found in cache Optional is empty.
     *
     * Subscribe: Does not operate by default on a particular scheduler
     * Error: no
     *
     */
    fun findCachedBitcoinMarketPrices(periodBeforeToday: TimePeriod): Single<Optional<BitcoinPricesRequestResult>>

    /**
     * Puts [BitcoinPricesRequestResult] in a cache.
     *
     * @param periodBeforeToday Requested period.
     * @param result Result to put
     *
     * @return Completable. Always complete.
     *
     * Subscribe: Does not operate by default on a particular scheduler
     * Error: no
     *
     */
    fun putBitcoinMarketPrices(periodBeforeToday: TimePeriod, result: BitcoinPricesRequestResult): Completable

}