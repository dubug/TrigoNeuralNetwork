package controller

import javafx.beans.binding.StringBinding

import model.function._

import scala.reflect.runtime.universe.typeOf
import scalafx.Includes._
import scalafx.application.{JFXApp, Platform}
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.{ComboBox, TextField}
import scalafxml.core.macros.sfxml
import scalafxml.core.{DependenciesByType, FXMLView}

/**
 * Created by Yves on 25.02.2015.
 */
@sfxml
class MainWindowController(
                            private val h1TextField: TextField,
                            private val h2TextField: TextField,
                            private val functionComboBox: ComboBox[MathematicalFunction],
                            private val mathematicalFunctions: MathematicalFunctions) {

  // Filling the combo box
  for (converter <- mathematicalFunctions.available) {
    functionComboBox += converter
  }
  functionComboBox.getSelectionModel.selectFirst()

  // Data binding
  h1TextField.text <== new StringBinding {
    bind(h2TextField.text.delegate, functionComboBox.getSelectionModel.selectedItemProperty)

    def computeValue() = {
      if (h1TextField.text.value != "")
        functionComboBox.getSelectionModel.getSelectedItem run h1TextField.text.value
      else "0.0"
    }
  }

  // Init button event handler
  def onInit(event: ActionEvent) {
    Platform.exit()
  }
}

object ScalaFXML extends JFXApp {
  val fxmlFilePath = "/view/MainWindow.fxml"

  val root = FXMLView(getClass.getResource(fxmlFilePath),
    new DependenciesByType(Map(
      typeOf[MathematicalFunctions] -> new MathematicalFunctions(Cosine, Sine, Complex, Chaos))))

  stage = new JFXApp.PrimaryStage() {
    title = "Neural network HARDCODED"
    scene = new Scene(root)
  }
}

