package com.example.bitcoinprice.data.blockchain_data.model.json

import com.example.bitcoinprice.model.GsonSerializable

enum class Status : GsonSerializable {
    ok,
    `not-found`,
}