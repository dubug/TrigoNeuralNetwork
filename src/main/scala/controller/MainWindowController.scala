package controller

import grizzled.slf4j.Logging
import model.function._
import util.Converters

import scala.reflect.runtime.universe.typeOf
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.chart.LineChart
import scalafx.scene.control.{ComboBox, Label, TextField}
import scalafxml.core.macros.sfxml
import scalafxml.core.{DependenciesByType, FXMLView}

/**
 * Controller for the main window (MVC).
 *
 * Created by Yves on 25.02.2015.
 */
@sfxml
class MainWindowController(private val h1TextField: TextField,
                           private val h2TextField: TextField,
                           private val h3TextField: TextField,
                           private val inputsTextField: TextField,
                           private val outputsTextField: TextField,
                           private val deltaTextField: TextField,
                           private val functionLineChart: LineChart[Number, Number],
                           private val momentumTextField: TextField,
                           private val learningRateTextField: TextField,
                           private val iterationsTextField: TextField,
                           private val delayTextField: TextField,
                           private val displayTotalIterationsLabel: Label,
                           private val functionComboBox: ComboBox[MathematicalFunction],
                           private val mathematicalFunctions: MathematicalFunctions) extends Logging {
  var hiddenLayers = Vector(1, 0, 0)

  //  @javafx.fxml.FXML
  //  var functionLineChart: LineChart[Number, Number]=null

  // Filling the combo box
  for (converter <- mathematicalFunctions.available) {
    functionComboBox += converter
  }
  functionComboBox.getSelectionModel.selectFirst()

  // Create the line chart
  var managedFunctionLineChart = new ManageFunctionLineChart(functionLineChart, functionComboBox.getSelectionModel.getSelectedItem)

  // Data binding between the functionComboBox and H2 and H1 text fields
  //  h1TextField.text <== new StringBinding {
  //    bind(h2TextField.text.delegate, functionComboBox.getSelectionModel.selectedItemProperty)
  //
  //    def computeValue() = {
  //      debug("Function Combo Box and H2 field => computeValue()")
  //      if (h1TextField.text.value != "")
  //        functionComboBox.getSelectionModel.getSelectedItem run h1TextField.text.value
  //      else "0.0"
  //    }
  //  }

  /**
   * Buttons event handlers
   */

  def onInit(event: ActionEvent) {
    debug("Init button")
  }

  def onLearn(event: ActionEvent) {
    debug("Learn button")
  }

  /**
   * Combo box event handler
   */

  // The following conflicts with the data binding above
  def onFunctionComboBox(event: ActionEvent) {
    debug("Function Combo Box")
    managedFunctionLineChart = new ManageFunctionLineChart(functionLineChart, functionComboBox.getSelectionModel.getSelectedItem)
  }

  /**
   * Text fields event handlers
   */
  def onH1TextField(event: ActionEvent) {
    debug("H1 text field event handler")
    // Why do I have to use asInstanceOf when the method returns always Int?
    hiddenLayers = hiddenLayers.updated(0, Converters.stringToPositiveInt(h1TextField.text.value))
    h1TextField.setText(hiddenLayers(0).toString)
    logger.debug("layers" + hiddenLayers)
  }

  def onH2TextField(event: ActionEvent) {
    debug("H2 text field event handler")
    hiddenLayers = hiddenLayers.updated(1, Converters.stringToPositiveInt(h2TextField.text.value))
    h1TextField.setText(hiddenLayers(1).toString)
    debug("layers" + hiddenLayers)
  }

  def onH3TextField(event: ActionEvent) {
    debug("H3 text field event handler")
    hiddenLayers = hiddenLayers.updated(2, Converters.stringToPositiveInt(h3TextField.text.value))
    h1TextField.setText(hiddenLayers(2).toString)
    debug("layers" + hiddenLayers)
  }
}

object ScalaFXML extends JFXApp {
  // HARDCODED
  val fxmlFilePath = "/view/MainWindow.fxml"

  val root = FXMLView(getClass.getResource(fxmlFilePath),
    new DependenciesByType(Map(
      typeOf[MathematicalFunctions] -> new MathematicalFunctions(Cosine, Sine, Complex, Chaos))))

  stage = new JFXApp.PrimaryStage() {
    title = "Neural network HARDCODED"
    scene = new Scene(root)
  }
}

