package com.example.bitcoinprice.model.data.bitcoin_price

data class BitcoinPricesRequestResult(

    val resultCode: BitcoinPricesRequestResultCode,
    val data: BitcoinPricesRequestResultData?

)