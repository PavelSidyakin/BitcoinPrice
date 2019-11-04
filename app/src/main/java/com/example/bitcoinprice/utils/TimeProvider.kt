package com.example.bitcoinprice.utils

interface TimeProvider {

    /**
     * Returns current system time in milliseconds.
     */
    fun getCurrentTimeMillis(): Long
}