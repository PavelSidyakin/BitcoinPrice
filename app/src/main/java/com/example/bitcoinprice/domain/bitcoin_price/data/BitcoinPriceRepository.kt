package com.example.bitcoinprice.domain.bitcoin_price.data

import com.example.bitcoinprice.model.data.bitcoin_price.BitcoinPricesRequestResult
import com.example.bitcoinprice.model.domain.TimePeriod
import io.reactivex.Single

interface BitcoinPriceRepository {

    /**
     * Requests bitcoin marker prices.
     *
     * @param periodBeforeToday Requested period.
     *
     * @return Single with request result.
     *
     * Subscribe: io
     * Error: no
     *
     */
    fun requestBitcoinMarketPrices(periodBeforeToday: TimePeriod): Single<BitcoinPricesRequestResult>

}