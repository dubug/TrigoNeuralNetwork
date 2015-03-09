package controller

import grizzled.slf4j.Logging
import model.function._
import model.neuralnetwork.{NeuralNetworkTrainer, Neuron}
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
                           private val delayTextField: TextField,
                           private val functionLineChart: LineChart[Number, Number],
                           private val momentumTextField: TextField,
                           private val learningRateTextField: TextField,
                           private val iterationsTextField: TextField,
                           private val counter: Label,
                           private val displayTotalIterationsLabel: Label,
                           private val functionComboBox: ComboBox[MathematicalFunction],
                           private val mathematicalFunctions: MathematicalFunctions) extends Logging {
  /**
   * A vector of integers to store the number of neurons per hidden layer.
   * This information is taken from the fields: h1TestField, h2TestField and h3TestField.
   */
  var hiddenLayers = Vector(1, 0, 0)
  var neuralNetworkTrainer: NeuralNetworkTrainer = null

  // Filling the combo box
  for (mathematicalFunction <- mathematicalFunctions.available) {
    functionComboBox += mathematicalFunction
  }
  functionComboBox.getSelectionModel.selectFirst()

  // Create the line chart
  var managedFunctionLineChart = new ManageFunctionLineChart(functionLineChart,
    functionComboBox.getSelectionModel.getSelectedItem,
    null,
    null)

  // Data binding between the functionComboBox and H2 and H1 text fields
  //  h1TextField.text <== new StringBinding {
  //    bind(h2TextField.text.delegate, functionComboBox.getSelectionModel.selectedItemProperty)
  //
  //    def computeValue() = {
  //      debug("Function Combo Box and H2 field => computeValue()")
  //      if (h1TextField.text.value != "")
  //        functionComboBox.getSelectionModel.getSelectedItem apply h1TextField.text.value
  //      else "0.0"
  //    }
  //  }

  /**
   * Buttons event handlers
   */

  def onInit(event: ActionEvent) {
    debug("Init button")
    var hiddenLayers = new Array[Int](3)
    hiddenLayers(0) = Converters.stringToPositiveInt(h1TextField.delegate.text())
    hiddenLayers(1) = Converters.stringToPositiveInt(h2TextField.delegate.text())
    hiddenLayers(2) = Converters.stringToPositiveInt(h3TextField.delegate.text())
    info("Size of the hidden layer number 0: " + hiddenLayers(0))
    info("Size of the hidden layer number 0: " + hiddenLayers(1))
    info("Size of the hidden layer number 0: " + hiddenLayers(2))

    neuralNetworkTrainer = new NeuralNetworkTrainer(Converters.stringToPositiveInt(inputsTextField.delegate.text()),
      hiddenLayers,
      Converters.stringToPositiveInt(outputsTextField.delegate.text()),
      functionComboBox.getSelectionModel.getSelectedItem,
      Converters.stringToPositiveDouble(deltaTextField.delegate.text()),
      Converters.stringToPositiveDouble(delayTextField.delegate.text()))

    displayTotalIterationsLabel.setText("0")
    //if (errorGraph != null) errorGraph.graph.clear
  }

  def onLearn(event: ActionEvent) {
    debug("Learn button")
    neuralNetworkTrainer.setSamples()
    Neuron.learningRate = Converters.stringToPositiveDouble(learningRateTextField.delegate.text())
    var max: Int = Converters.stringToPositiveInt(iterationsTextField.delegate.text())
    counter.setDisable(false)
    disableDuringLearning(true)
    for (i <- 0 until max) {
      neuralNetworkTrainer.learn()
      if (i % 10 == 0) {
        counter.setText(String.valueOf(max - i))
        // Handle error graph here
      }
      neuralNetworkTrainer.test()
      // Handle error graph here
    }
    // Update total iterations
    displayTotalIterationsLabel.setText(String.valueOf(Converters.stringToPositiveInt(displayTotalIterationsLabel.delegate.text()) + max))
    disableDuringLearning(false)
  }

  /**
   * Disables the text fields and controls during training.
   */
  def disableDuringLearning(toggle: Boolean) {
    h1TextField.setDisable(toggle)
    h2TextField.setDisable(toggle)
    h3TextField.setDisable(toggle)
    inputsTextField.setDisable(toggle)
    outputsTextField.setDisable(toggle)
    deltaTextField.setDisable(toggle)
    delayTextField.setDisable(toggle)
    momentumTextField.setDisable(toggle)
    learningRateTextField.setDisable(toggle)
    iterationsTextField.setDisable(toggle)
  }

  /**
   * Combo box event handler
   */

  // The following conflicts with the data binding above
  def onFunctionComboBox(event: ActionEvent) {
    debug("Function Combo Box")
    managedFunctionLineChart = new ManageFunctionLineChart(functionLineChart,
      functionComboBox.getSelectionModel.getSelectedItem,
      if (neuralNetworkTrainer == null) null else neuralNetworkTrainer.inputValues,
      if (neuralNetworkTrainer == null) null else neuralNetworkTrainer.outputValues)
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

