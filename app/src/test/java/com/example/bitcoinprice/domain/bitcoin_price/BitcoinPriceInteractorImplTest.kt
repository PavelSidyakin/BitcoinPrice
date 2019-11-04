package com.example.bitcoinprice.domain.bitcoin_price

import com.example.bitcoinprice.domain.bitcoin_price.data.BitcoinPriceCacheRepository
import com.example.bitcoinprice.domain.bitcoin_price.data.BitcoinPriceRepository
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPriceRequestDataPoint
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResultCode
import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResultData
import com.example.bitcoinprice.model.domain.TimePeriod
import com.example.bitcoinprice.model.domain.TimePeriodUnit
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPriceDataPoint
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResult
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResultCode
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResultData
import com.example.bitcoinprice.utils.SchedulersProviderStub
import com.example.bitcoinprice.utils.logs.LogWrapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.Optional

class `BitcoinPriceInteractorImpl Tests` {

    @Mock
    private lateinit var bitcoinPriceCacheRepository: BitcoinPriceCacheRepository
    @Mock
    private lateinit var bitcoinPriceRepository: BitcoinPriceRepository

    private lateinit var bitcoinPriceInteractor: BitcoinPriceInteractorImpl

    // Sample test data
    private val timePeriod = TimePeriod(22, TimePeriodUnit.YEAR)

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

    private val resultData = BitcoinPricesResultData(
        listOf(
            BitcoinPriceDataPoint(point0Time, point0Price),
            BitcoinPriceDataPoint(point1Time, point1Price)
        )
    )

    @BeforeEach
    fun beforeEachTest() {
        LogWrapper.enabled = false

        MockitoAnnotations.initMocks(this)

        bitcoinPriceInteractor = BitcoinPriceInteractorImpl(
            bitcoinPriceCacheRepository,
            bitcoinPriceRepository,
            SchedulersProviderStub()
        )

    }

    @Nested
    inner class `When result is not found in cache` {

        @BeforeEach
        fun beforeEachTest() {
            // when
            whenever(bitcoinPriceCacheRepository.findCachedBitcoinMarketPrices(any()))
                .thenReturn(Single.just(Optional.empty()))
            whenever(bitcoinPriceCacheRepository.putBitcoinMarketPrices(any(), any()))
                .thenReturn(Completable.complete())

        }

        @Test
        fun `real request should be performed`() {
            // action
            bitcoinPriceInteractor.requestBitcoinMarketPrices(timePeriod)
                .test()
                .await()

            // verify
            verify(bitcoinPriceRepository).requestBitcoinMarketPrices(timePeriod)
        }


        @Nested
        inner class `when real request failed` {

            @Test
            fun `with GENERAL_ERROR, should return GENERAL_ERROR`() {
                // when
                whenever(bitcoinPriceRepository.requestBitcoinMarketPrices(timePeriod))
                    .thenReturn(
                        Single.just(
                            BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.GENERAL_ERROR, null)
                        )
                    )

                // action
                bitcoinPriceInteractor.requestBitcoinMarketPrices(timePeriod)
                    .test()
                    .await()
                    .assertComplete()
                    .assertResult(BitcoinPricesResult(BitcoinPricesResultCode.GENERAL_ERROR, null))
            }

            @Test
            fun `with NETWORK_ERROR, should return NETWORK_ERROR`() {
                // when
                whenever(bitcoinPriceRepository.requestBitcoinMarketPrices(timePeriod))
                    .thenReturn(
                        Single.just(
                            BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.NETWORK_ERROR, null)
                        )
                    )

                // action
                bitcoinPriceInteractor.requestBitcoinMarketPrices(timePeriod)
                    .test()
                    .await()
                    .assertComplete()
                    .assertResult(BitcoinPricesResult(BitcoinPricesResultCode.NETWORK_ERROR, null))
            }

            @Test
            fun `with OK, but data is null, should return GENERAL_ERROR`() {
                // when
                whenever(bitcoinPriceRepository.requestBitcoinMarketPrices(timePeriod))
                    .thenReturn(
                        Single.just(
                            BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.OK, null)
                        )
                    )

                // action
                bitcoinPriceInteractor.requestBitcoinMarketPrices(timePeriod)
                    .test()
                    .await()
                    .assertComplete()
                    .assertResult(BitcoinPricesResult(BitcoinPricesResultCode.GENERAL_ERROR, null))
            }

            @AfterEach
            fun afterEachTest() {
                // verify
                verify(bitcoinPriceCacheRepository, never()).putBitcoinMarketPrices(any(), any())
            }

        }

        @Nested
        inner class `when real request OK` {

            @BeforeEach
            fun beforeEachTest() {

                // when
                whenever(bitcoinPriceRepository.requestBitcoinMarketPrices(timePeriod))
                    .thenReturn(
                        Single.just(
                            BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.OK, requestResultData)
                        )
                    )

            }

            @Test
            fun `should return the same data`() {

                // action
                bitcoinPriceInteractor.requestBitcoinMarketPrices(timePeriod)
                    .test()
                    .await()
                    .assertComplete()
                    .assertResult(BitcoinPricesResult(BitcoinPricesResultCode.OK, resultData))
            }

            @AfterEach
            fun afterEachTest() {
                // verify
                verify(bitcoinPriceCacheRepository).putBitcoinMarketPrices(
                    timePeriod,
                    BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.OK, requestResultData)
                )
            }

        }

        @AfterEach
        fun afterEachTest() {
            // verify
            verify(bitcoinPriceCacheRepository).findCachedBitcoinMarketPrices(timePeriod)
        }

    }

    @Nested
    inner class `When result is found in cache` {

        @BeforeEach
        fun beforeEachTest() {
            // when
            whenever(bitcoinPriceCacheRepository.findCachedBitcoinMarketPrices(any()))
                .thenReturn(
                    Single.just(
                        Optional.of(BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.OK, requestResultData))
                    )
                )
            whenever(bitcoinPriceCacheRepository.putBitcoinMarketPrices(any(), any()))
                .thenReturn(Completable.complete())
            whenever(bitcoinPriceRepository.requestBitcoinMarketPrices(timePeriod))
                .thenReturn(
                    Single.just(
                        BitcoinPricesRequestResult(BitcoinPricesRequestResultCode.GENERAL_ERROR, null)
                    )
                )
        }

        @Test
        fun `should return cached result`() {
            // action
            bitcoinPriceInteractor.requestBitcoinMarketPrices(timePeriod)
                .test()
                .await()
                .assertComplete()
                .assertResult(BitcoinPricesResult(BitcoinPricesResultCode.OK, resultData))
        }

        @AfterEach
        fun afterEachTest() {
            // verify
            verify(bitcoinPriceRepository, never()).requestBitcoinMarketPrices(any())
            verify(bitcoinPriceCacheRepository, never()).putBitcoinMarketPrices(any(), any())
        }

    }

}