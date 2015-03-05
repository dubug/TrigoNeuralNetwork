package controller

import model.function.MathematicalFunction

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{XYChart, LineChart}
import scalafx.scene.chart.XYChart.Series

/**
 * Class to draw a line chart.
 *
 * Created by Yves on 02.03.2015.
 */
class ManageFunctionLineChart(val lineChart: LineChart[Number, Number],val function:MathematicalFunction){
  var buf=for (x <- 0.0 to 2.0 by 0.01) yield (x, function.run(x))
  val data = ObservableBuffer(buf map {case (x, y) => XYChart.Data[Number, Number](x, y)} )
  val mySeries = XYChart.Series[Number, Number](function.toString, data)
  lineChart.getData().clear()
  lineChart.getData().add(mySeries)
}
