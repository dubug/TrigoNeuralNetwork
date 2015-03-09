package controller

import model.function.MathematicalFunction

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{LineChart, XYChart}

/**
 * Class to draw a line chart.
 *
 * Created by Yves on 02.03.2015.
 */
class ManageFunctionLineChart(val lineChart: LineChart[Number, Number],
                              val function: MathematicalFunction,
                              var inputValues: IndexedSeq[(Double,Double)],
                              var outputValues: IndexedSeq[(Double,Double)]) {
  var buf = for (x <- 0.0 to 2.0 by 0.01) yield (x, function(x))
  val data = ObservableBuffer(buf map { case (x, y) => XYChart.Data[Number, Number](x, y)})
  val functionSerie = XYChart.Series[Number, Number](function.toString, data)
  lineChart.getData().clear()
  lineChart.getData().add(functionSerie)

  if (inputValues != null) {
    // Add the input values given to the perceptron as a serie
    val dataInput = ObservableBuffer(inputValues map { case (x, y) => XYChart.Data[Number, Number](x, y)})
    val inputSerie = XYChart.Series[Number, Number]("Input", data)
    lineChart.getData().add(inputSerie)
  }
  if (outputValues != null) {
    // Add the output values reeived from the perceptron as a serie
    val dataOutput = ObservableBuffer(outputValues map { case (x, y) => XYChart.Data[Number, Number](x, y)})
    val outputSerie = XYChart.Series[Number, Number]("Output", data)
    lineChart.getData().add(outputSerie)
  }
}
