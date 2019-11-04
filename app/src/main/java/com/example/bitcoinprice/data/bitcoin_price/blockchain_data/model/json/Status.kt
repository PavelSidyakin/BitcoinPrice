package com.example.bitcoinprice.data.bitcoin_price.blockchain_data.model.json

import com.example.bitcoinprice.model.GsonSerializable

enum class Status : GsonSerializable {

    ok,
    `not-found`,

}