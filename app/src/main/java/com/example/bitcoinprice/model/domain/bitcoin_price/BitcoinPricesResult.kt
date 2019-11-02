package com.example.bitcoinprice.model.domain.bitcoin_price

data class BitcoinPricesResult (
    val resultCode: BitcoinPricesResultCode,
    val data: BitcoinPricesResultData?
)