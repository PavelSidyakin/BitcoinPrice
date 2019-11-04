package com.example.bitcoinprice.data.bitcoin_price

import android.util.LruCache
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPriceRequestDataPoint
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResultCode
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResultData
import com.example.bitcoinprice.model.domain.TimePeriod
import com.example.bitcoinprice.model.domain.TimePeriodUnit
import com.example.bitcoinprice.utils.TimeProvider
import com.example.bitcoinprice.utils.logs.LogWrapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.Optional


class `BitcoinPriceCacheRepositoryImpl Test` {

    @Mock
    private lateinit var lruCache: LruCache<TimePeriod, BitcoinPriceCacheRepositoryImpl.CacheItem>
    @Mock
    private lateinit var timeProvider: TimeProvider

    @InjectMocks
    private lateinit var repository: BitcoinPriceCacheRepositoryImpl

    // Sample test data
    private val timePeriod = TimePeriod(11, TimePeriodUnit.MONTH)
    private val point0Time = 111L
    private val point0Price = 11.0

    private val point1Time = 222L
    private val point1Price = 22.0

    private val requestResultData = BitcoinPricesRequestResultData(
        listOf(
            BitcoinPriceRequestDataPoint(point0Time, point0Price),
            BitcoinPriceRequestDataPoint(point1Time, point1Price)
        )
    )
    private val requestResult = BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.OK, requestResultData)

    @BeforeEach
    fun beforeEachTest() {
        LogWrapper.enabled = false
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `When putBitcoinMarketPrices() called, should add value to the cache`() {
        val itemTimestamp = 1L
        // when
        whenever(timeProvider.getCurrentTimeMillis())
            .thenReturn(itemTimestamp)

        // action
        repository.putBitcoinMarketPrices(timePeriod, requestResult)
            .test()
            .await()
            .assertComplete()

        // verify
        verify(lruCache).put(timePeriod, BitcoinPriceCacheRepositoryImpl.CacheItem(itemTimestamp, requestResult))
    }

    @Test
    fun `When findCachedBitcoinMarketPrices() called and value is not expired, should return result`() {
        val itemTimestamp = 1L
        // when
        whenever(timeProvider.getCurrentTimeMillis())
            .thenReturn(BitcoinPriceCacheRepositoryImpl.MAX_LIVE_TIME_OF_RESULT_MS - 1L)
        whenever(lruCache.get(any()))
            .thenReturn(BitcoinPriceCacheRepositoryImpl.CacheItem(itemTimestamp, requestResult))

        // action
        repository.findCachedBitcoinMarketPrices(timePeriod)
            .test()
            .await()
            .assertComplete()
            .assertResult(Optional.of(requestResult))

    }

    @Test
    fun `When findCachedBitcoinMarketPrices() called and value is expired, should not return result`() {
        val itemTimestamp = 1L
        // when
        whenever(timeProvider.getCurrentTimeMillis())
            .thenReturn(BitcoinPriceCacheRepositoryImpl.MAX_LIVE_TIME_OF_RESULT_MS + 100L)
        whenever(lruCache.get(any()))
            .thenReturn(BitcoinPriceCacheRepositoryImpl.CacheItem(itemTimestamp, requestResult))

        // action
        repository.findCachedBitcoinMarketPrices(timePeriod)
            .test()
            .await()
            .assertComplete()
            .assertResult(Optional.empty())

        // verify
        verify(lruCache).remove(timePeriod)
    }

}