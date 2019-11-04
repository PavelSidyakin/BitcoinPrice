package com.example.bitcoinprice.utils

import javax.inject.Inject

class TimeProviderImpl
@Inject
constructor() : TimeProvider {

    override fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

}