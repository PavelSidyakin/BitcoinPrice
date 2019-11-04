package com.example.bitcoinprice.utils.logs

import com.example.bitcoinprice.BuildConfig

/**
 * Writes log in logcat in debug build.
 *
 * Calling of a logs functions will be cut in release build (prevent creation of a log message in release build).
 *
 */
inline fun log(block: LogWrapper.() -> Unit) {
    if (BuildConfig.DEBUG) LogWrapper.block()
}
