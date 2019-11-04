package com.example.bitcoinprice.presentation.bitcoin_price.presenter

import com.example.bitcoinprice.domain.bitcoin_price.BitcoinPriceInteractor
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPriceDataPoint
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResult
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResultCode
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResultData
import com.example.bitcoinprice.presentation.bitcoin_price.model.DisplayPeriod
import com.example.bitcoinprice.presentation.bitcoin_price.view.BitcoinPriceGraphView
import com.example.bitcoinprice.utils.SchedulersProviderStub
import com.example.bitcoinprice.utils.logs.LogWrapper
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.Utils
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatcher
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlin.math.abs


class `BitcoinPriceGraphPresenter Tests` {

    @Mock
    private lateinit var bitcoinPriceInteractor: BitcoinPriceInteractor
    @Mock
    private lateinit var view: BitcoinPriceGraphView

    private lateinit var presenter: BitcoinPriceGraphPresenter

    // Sample test data
    private val pointsTime = listOf(111L, 222L)
    private val pointsPrice = listOf(11.0, 22.0)

    private val resultData = BitcoinPricesResultData(
        listOf(
            BitcoinPriceDataPoint(pointsTime[0], pointsPrice[0]),
            BitcoinPriceDataPoint(pointsTime[1], pointsPrice[1])
        ))
    private val expectedListOfEntries = listOf(
        Entry(pointsTime[0].toFloat(), pointsPrice[0].toFloat(), BitcoinPriceDataPoint(pointsTime[0], pointsPrice[0])),
        Entry(pointsTime[1].toFloat(), pointsPrice[1].toFloat(), BitcoinPriceDataPoint(pointsTime[1], pointsPrice[1]))
    )

    private val displayPeriod = DisplayPeriod.MONTH_3

    @BeforeEach
    fun beforeEachTest() {
        LogWrapper.enabled = false
        MockitoAnnotations.initMocks(this)
        setupPresenter()

    }

    @Nested
    inner class `When requestBitcoinMarketPrices() failed` {

        @Test
        fun `with GENERAL_ERROR, should display general error message`() {
            // when
            whenever(bitcoinPriceInteractor.requestBitcoinMarketPrices(any()))
                .thenReturn(Single.just(BitcoinPricesResult(BitcoinPricesResultCode.GENERAL_ERROR, null)))

            // action
            presenter.onDisplayPeriodSelected(displayPeriod)

            // verify
            verify(view).showGeneraError(true)
            verify(view, never()).showNetworkError(true)
        }

        @Test
        fun `with NETWORK_ERROR, should display network error message`() {
            // when
            whenever(bitcoinPriceInteractor.requestBitcoinMarketPrices(any()))
                .thenReturn(Single.just(BitcoinPricesResult(BitcoinPricesResultCode.NETWORK_ERROR, null)))

            // action
            presenter.onDisplayPeriodSelected(displayPeriod)

            // verify
            verify(view).showNetworkError(true)
            verify(view, never()).showGeneraError(true)
        }

        @AfterEach
        fun afterEachTest() {
            verify(view, never()).setMaxPriceInPeriod(any())
            verify(view, never()).setMinPriceInPeriod(any())
            verify(view, never()).setAveragePriceInPeriod(any())
            verify(view, never()).setGraphPoints(any())
        }
    }

    @Nested
    inner class `When requestBitcoinMarketPrices() OK` {
        @BeforeEach
        fun beforeEachTest() {
            // when
            whenever(bitcoinPriceInteractor.requestBitcoinMarketPrices(any()))
                .thenReturn(Single.just(BitcoinPricesResult(BitcoinPricesResultCode.OK, resultData)))
        }

        @Test
        fun `should display graph and info with corresponding data`() {
            // action
            presenter.onDisplayPeriodSelected(displayPeriod)

            // verify
            verifyGraphDataAndInfoData()
            verify(view, never()).showGeneraError(true)
            verify(view, never()).showNetworkError(true)
        }

    }

    @Nested
    inner class `When requestBitcoinMarketPrices() failed twice, then OK` {
        @BeforeEach
        fun beforeEachTest() {
            // when
            whenever(bitcoinPriceInteractor.requestBitcoinMarketPrices(any()))
                .thenReturn(Single.just(BitcoinPricesResult(BitcoinPricesResultCode.GENERAL_ERROR, null)))
                .thenReturn(Single.just(BitcoinPricesResult(BitcoinPricesResultCode.NETWORK_ERROR, null)))
                .thenReturn(Single.just(BitcoinPricesResult(BitcoinPricesResultCode.OK, resultData)))

        }

        @Test
        fun `should display error and retry on click on error`() {
            // action 1
            presenter.onDisplayPeriodSelected(displayPeriod)

            // verify 1
            verify(view).showGeneraError(true)

            // action 2
            presenter.retry()

            // verify 2
            verify(view).showNetworkError(true)

            // action 3
            presenter.retry()

            // verify 3
            verifyGraphDataAndInfoData()
        }

        @Test
        fun `should display error and retry on select another period`() {
            // action 1
            presenter.onDisplayPeriodSelected(DisplayPeriod.MONTH_3)

            // verify 1
            verify(view).showGeneraError(true)

            // action 2
            presenter.onDisplayPeriodSelected(DisplayPeriod.MONTH_1)

            // verify 2
            verify(view).showNetworkError(true)

            // action 3
            presenter.onDisplayPeriodSelected(DisplayPeriod.WEEK_1)

            // verify 3
            verifyGraphDataAndInfoData()
        }

    }

    @AfterEach
    fun afterEachTest() {
        val progressInOrder = Mockito.inOrder(view)
        progressInOrder.verify(view).showLoadingProgress(true)
        progressInOrder.verify(view).showLoadingProgress(false)

        verify(view, atLeastOnce()).showNetworkError(false)
        verify(view, atLeastOnce()).showGeneraError(false)
        verify(view, times(DisplayPeriod.values().size)).addDisplayPeriod(any())
        verify(view).selectDisplayPeriod(displayPeriod)
    }

    private fun verifyGraphDataAndInfoData() {
        // verify
        verify(view).setGraphPoints(argThat(ArgumentMatcher { entryList ->
            return@ArgumentMatcher areListsOfEntryEqual(entryList, expectedListOfEntries)
        }))

        verify(view).setMaxPriceInPeriod(pointsPrice.max()!!)
        verify(view).setMinPriceInPeriod(pointsPrice.min()!!)
        verify(view).setAveragePriceInPeriod(pointsPrice.average())
    }

    private fun setupPresenter() {
        presenter = BitcoinPriceGraphPresenter(bitcoinPriceInteractor, SchedulersProviderStub(), null)
        presenter.attachView(view)
    }

    private fun areListsOfEntryEqual(list1: List<Entry>, list2: List<Entry>): Boolean {
        return list1.zip(list2)
            .all { it.first.isEqualTo(it.second) }
    }

    // Taken from com.github.mikephil.charting.data.Entry.equalTo and fixed.
    // The original function compares references to data (not content).
    private fun Entry.isEqualTo(e: Entry?): Boolean {

        if (e == null)
            return false

        if (e.data != this.data)
            return false

        if (abs(e.x - this.x) > Utils.FLOAT_EPSILON)
            return false

        return abs(e.y - this.y) <= Utils.FLOAT_EPSILON

    }


}