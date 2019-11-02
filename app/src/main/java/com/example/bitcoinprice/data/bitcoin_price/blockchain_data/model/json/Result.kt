package com.example.bitcoinprice.data.bitcoin_price.blockchain_data.model.json

import com.example.bitcoinprice.model.GsonSerializable

data class Result (

    val status: Status,
    val name: String?,
    val period: String?,
    val description: String?,
    val error: String?,
    val values: List<DataPoint>?

) : GsonSerializable