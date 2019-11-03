package com.example.bitcoinprice.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bitcoinprice.R
import com.example.bitcoinprice.presentation.bitcoin_price.view.BitcoinPriceGraphFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.main_activity_container, BitcoinPriceGraphFragment())

        fragmentTransaction.commit()
    }

}
