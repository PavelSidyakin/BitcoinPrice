package com.example.bitcoinprice.domain.bitcoin_price

import com.example.bitcoinprice.model.domain.TimePeriod
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPricesResult
import io.reactivex.Single

interface BitcoinPriceInteractor {

    /**
     * Requests bitcoin marker prices.
     *
     * @param periodBeforeToday Requested period.
     *
     * @return Single with request result.
     *
     * Subscribe: io
     * Error: no. If error is occurred then [BitcoinPricesResult.resultCode] is not OK
     *
     */
    fun requestBitcoinMarketPrices(periodBeforeToday: TimePeriod): Single<BitcoinPricesResult>

}