package com.example.bitcoinprice.data.bitcoin_price

import android.util.LruCache
import androidx.annotation.VisibleForTesting
import com.example.bitcoinprice.domain.bitcoin_price.data.BitcoinPriceCacheRepository
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import com.example.bitcoinprice.model.domain.TimePeriod
import com.example.bitcoinprice.utils.TimeProvider
import com.example.bitcoinprice.utils.logs.log
import io.reactivex.Completable
import io.reactivex.Single
import java.util.Optional
import javax.inject.Inject

class BitcoinPriceCacheRepositoryImpl
constructor(
    private val lruCache: LruCache<TimePeriod, CacheItem>,
    private val timeProvider: TimeProvider
) : BitcoinPriceCacheRepository {

    @Inject
    constructor(timeProvider: TimeProvider) : this(LruCache<TimePeriod, CacheItem>(7), timeProvider)

    override fun findCachedBitcoinMarketPrices(periodBeforeToday: TimePeriod): Single<Optional<BitcoinPricesRequestResult>> {
        return Single.fromCallable { Optional.ofNullable(lruCache[periodBeforeToday]) }
            .map { cacheItem -> processCacheItem(periodBeforeToday, cacheItem) }
            .doOnSubscribe { log { i(TAG, "BitcoinPriceCacheRepositoryImpl.findCachedBitcoinMarketPrices(): Subscribe. periodBeforeTodayDays = [${periodBeforeToday}]") } }
            .doOnSuccess { log { i(TAG, "BitcoinPriceCacheRepositoryImpl.findCachedBitcoinMarketPrices(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "BitcoinPriceCacheRepositoryImpl.findCachedBitcoinMarketPrices(): Error", it) } }
    }

    private fun processCacheItem(periodBeforeToday: TimePeriod, cacheItem: Optional<CacheItem>): Optional<BitcoinPricesRequestResult> {
        if (cacheItem.isPresent) {
            log { i(TAG, "BitcoinPriceCacheRepositoryImpl.processCacheItem(). found cacheItem = [${cacheItem}]") }
            if ((timeProvider.getCurrentTimeMillis() - cacheItem.get().timestamp) <= MAX_LIVE_TIME_OF_RESULT_MS) {
                return Optional.of(cacheItem.get().result)
            } else {
                log { i(TAG, "BitcoinPriceCacheRepositoryImpl.processCacheItem(). cacheItem is expired") }
                lruCache.remove(periodBeforeToday)
            }
        } else {
            log { i(TAG, "BitcoinPriceCacheRepositoryImpl.processCacheItem(). cacheItem is not found") }
        }

        return Optional.empty()
    }

    override fun putBitcoinMarketPrices(periodBeforeToday: TimePeriod, result: BitcoinPricesRequestResult): Completable {
        return Completable.fromCallable { lruCache.put(periodBeforeToday, CacheItem(timeProvider.getCurrentTimeMillis(), result)) }
            .doOnSubscribe { log { i(TAG, "BitcoinPriceCacheRepositoryImpl.putBitcoinMarketPrices(): Subscribe. periodBeforeTodayDays = [${periodBeforeToday}], result = [${result}]") } }
            .doOnComplete { log { i(TAG, "BitcoinPriceCacheRepositoryImpl.putBitcoinMarketPrices(): Complete") } }
            .doOnError { log { w(TAG, "BitcoinPriceCacheRepositoryImpl.putBitcoinMarketPrices(): Error", it) } }
            .onErrorComplete()
    }

    @VisibleForTesting
    data class CacheItem(
        val timestamp: Long,
        val result: BitcoinPricesRequestResult
    )

    @VisibleForTesting
    companion object {
        private const val TAG = "BitcoinPriceCache"

        @VisibleForTesting
        const val MAX_LIVE_TIME_OF_RESULT_MS = 1000 * 60 * 10
    }
}