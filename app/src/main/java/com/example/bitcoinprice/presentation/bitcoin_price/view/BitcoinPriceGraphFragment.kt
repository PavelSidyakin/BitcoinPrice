package com.example.bitcoinprice.presentation.bitcoin_price.view

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.bitcoinprice.R
import com.example.bitcoinprice.TheApplication
import com.example.bitcoinprice.model.domain.bitcoin_price.BitcoinPriceDataPoint
import com.example.bitcoinprice.presentation.bitcoin_price.model.DisplayPeriod
import com.example.bitcoinprice.presentation.bitcoin_price.presenter.BitcoinPriceGraphPresenter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.layout_bitcoin_price_graph.chip_group_choose_period_bitcoin_price_graph
import kotlinx.android.synthetic.main.layout_bitcoin_price_graph.line_chart_bitcoin_price_graph
import kotlinx.android.synthetic.main.layout_bitcoin_price_graph.progress_loading_bitcoin_price_graph
import kotlinx.android.synthetic.main.layout_bitcoin_price_graph_marker.view.text_view_date_marker_bitcoin_graph
import kotlinx.android.synthetic.main.layout_bitcoin_price_graph_marker.view.text_view_price_marker_bitcoin_graph
import java.math.RoundingMode
import java.text.DateFormat
import java.util.Date


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chart: LineChart = line_chart_bitcoin_price_graph

        chart.setNoDataText(null)
        chart.description = Description().apply { text = "" }

        chart.xAxis.position = XAxisPosition.BOTTOM
        chart.xAxis.valueFormatter = XAxisValueFormatter()
        chart.xAxis.textColor = getColorFromTheme(R.attr.color_bitcoin_graph_axis_text_date)

        chart.axisLeft.textColor = getColorFromTheme(R.attr.color_bitcoin_graph_axis_text_price)

        chart.axisRight.textColor = getColorFromTheme(R.attr.color_bitcoin_graph_axis_text_price)

        chart.legend.isEnabled = false

        chart.marker = PointMarker(chart.context)

        chart.setDrawMarkers(true)
    }

    override fun setGraphPoints(entries: List<Entry>) {

        val chart: LineChart = line_chart_bitcoin_price_graph

        val lineDataSet = LineDataSet(entries, null)

        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.color = getColorFromTheme(R.attr.color_bitcoin_graph_line)
//        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setDrawCircles(false)
//        lineDataSet.setDrawValues(false);
//        lineDataSet.setFillAlpha(65);
//        lineDataSet.setFillColor(ColorTemplate.getHoloBlue());
//        lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        lineDataSet.setDrawCircleHole(false)

        val lineData = LineData(lineDataSet)
        lineData.setDrawValues(false)

        chart.data = lineData

        chart.invalidate()

    }

    override fun addDisplayPeriod(displayPeriod: DisplayPeriod) {
        val chipGroup: ChipGroup = chip_group_choose_period_bitcoin_price_graph

        val chip = layoutInflater.inflate(R.layout.layout_bitcoin_price_graph_period_chip, chipGroup, false) as Chip

        chip.text = chipGroup.context.getString(displayPeriod.descriptionResId)
        chip.tag = displayPeriod

        chip.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                onChipSelected(view as Chip)
            }
        }

        chipGroup.addView(chip)
    }

    override fun setMaxPriceInPeriod(maxPrice: Double) {

    }

    override fun setMinPriceInPeriod(maxPrice: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAveragePriceInPeriod(averagePrice: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoadingProgress(show: Boolean) {
        progress_loading_bitcoin_price_graph.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun selectDisplayPeriod(displayPeriod: DisplayPeriod) {
        val chipGroup: ChipGroup = chip_group_choose_period_bitcoin_price_graph

        chipGroup.findViewWithTag<Chip>(displayPeriod)?.isChecked = true
    }

    private fun onChipSelected(chip: Chip) {
        presenter.onDisplayPeriodSelected(chip.tag as DisplayPeriod)

    }

    private fun formatTimestamp(timestamp: Long): String {
        return DateFormat.getDateInstance(DateFormat.SHORT)
            .format(Date(timestamp * 1000))
    }

    private fun formatPrice(price: Double): String {
        return String.format("%.2f", price.toBigDecimal().setScale(2, RoundingMode.HALF_UP))
    }

    private fun getColorFromTheme(color: Int): Int {
        return context?.run {
            val typedValue = TypedValue()
            theme.resolveAttribute(color, typedValue, true)
            typedValue.data
        } ?: 0
    }

    inner class PointMarker(context: Context) : MarkerView(context, R.layout.layout_bitcoin_price_graph_marker) {
        private val dateTextView = text_view_date_marker_bitcoin_graph
        private val priceTextView = text_view_price_marker_bitcoin_graph
        private val defaultOffset: MPPointF by lazy {
            MPPointF(-(width.toFloat() / 2), -height.toFloat())
        }

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            if (e == null) return

            val dataPoint: BitcoinPriceDataPoint = e.data as? BitcoinPriceDataPoint ?: return

            dateTextView.text = formatTimestamp(dataPoint.timeStamp)
            priceTextView.text = String.format(
                "%.2f", dataPoint.priceUsd.toBigDecimal()
                    .setScale(2, RoundingMode.HALF_UP)
            )

            super.refreshContent(e, highlight)
        }

        override fun getOffset(): MPPointF {
            return defaultOffset
        }

    }

    inner class XAxisValueFormatter() : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return formatTimestamp(value.toLong())
        }
    }

}