package com.example.bitcoinprice.presentation.bitcoin_price.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.bitcoinprice.R
import com.example.bitcoinprice.TheApplication
import com.example.bitcoinprice.presentation.bitcoin_price.presenter.BitcoinPriceGraphPresenter
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.layout_bitcoin_price_graph.line_chart_bitcoin_price_graph


class BitcoinPriceGraphFragment: MvpAppCompatFragment(), BitcoinPriceGraphView {

    @InjectPresenter
    lateinit var presenter: BitcoinPriceGraphPresenter

    @ProvidePresenter
    fun providePresenter(): BitcoinPriceGraphPresenter {
        return TheApplication.getAppComponent().getBitcoinPriceScreenComponent().getBitcoinPriceGraphPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bitcoin_price_graph, container, false)
    }

    override fun setGraphPoints(entries: List<Entry>) {

        val lineDataSet = LineDataSet(entries, null)

        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT;
        lineDataSet.color = Color.BLUE;
//        lineDataSet.setValueTextColor(ColorTemplate.getHoloBlue());
//        lineDataSet.setLineWidth(1.5f);
//        lineDataSet.setDrawCircles(false);
//        lineDataSet.setDrawValues(false);
//        lineDataSet.setFillAlpha(65);
//        lineDataSet.setFillColor(ColorTemplate.getHoloBlue());
//        lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
//        lineDataSet.setDrawCircleHole(false);

        val lineData = LineData(lineDataSet)
//        lineData.setValueTextColor(Color.BLUE);
//        lineData.setValueTextSize(9f);




        line_chart_bitcoin_price_graph.data = lineData
        line_chart_bitcoin_price_graph.description = Description().apply { text = "" }

        line_chart_bitcoin_price_graph.xAxis.position = XAxisPosition.BOTTOM
        line_chart_bitcoin_price_graph.xAxis.labelRotationAngle = 90f
        line_chart_bitcoin_price_graph.legend.isEnabled = false

        line_chart_bitcoin_price_graph.invalidate()

    }



}