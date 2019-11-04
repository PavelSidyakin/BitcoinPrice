package com.example.bitcoinprice.presentation.bitcoin_price.model

import com.example.bitcoinprice.R
import com.example.bitcoinprice.model.domain.TimePeriod
import com.example.bitcoinprice.model.domain.TimePeriodUnit

/**
 * Time periods displayed on UI.
 *
 */
enum class DisplayPeriod(val descriptionResId: Int, val timePeriod: TimePeriod) {

    DAY_3(R.string.bitcoin_price_graph_display_period_3_day, TimePeriod(3, TimePeriodUnit.DAY)),
    DAY_5(R.string.bitcoin_price_graph_display_period_5_day, TimePeriod(5, TimePeriodUnit.DAY)),
    WEEK_1(R.string.bitcoin_price_graph_display_period_1_week, TimePeriod(7, TimePeriodUnit.DAY)),
    WEEK_2(R.string.bitcoin_price_graph_display_period_2_week, TimePeriod(14, TimePeriodUnit.DAY)),
    MONTH_1(R.string.bitcoin_price_graph_display_period_1_month, TimePeriod(1, TimePeriodUnit.MONTH)),
    MONTH_3(R.string.bitcoin_price_graph_display_period_2_month, TimePeriod(2, TimePeriodUnit.MONTH)),
    YEAR_1(R.string.bitcoin_price_graph_display_period_1_year, TimePeriod(1, TimePeriodUnit.YEAR)),
    YEAR_3(R.string.bitcoin_price_graph_display_period_3_year, TimePeriod(3, TimePeriodUnit.YEAR)),

    ALL(R.string.bitcoin_price_graph_display_period_all, TimePeriod(0, TimePeriodUnit.ALL)),

}