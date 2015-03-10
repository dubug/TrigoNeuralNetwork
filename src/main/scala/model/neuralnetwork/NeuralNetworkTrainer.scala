package model.neuralnetwork

import grizzled.slf4j.Logging
import model.function.MathematicalFunction

/**
 * Provides the neural network with its inputs from a MathematicalFunction.
 * Gets the outputs to compare with what was expected.
 *
 * Created by Yves on 08.03.2015.
 */
class NeuralNetworkTrainer(numberOfInputNeurons: Int,
                           hiddenLayers: Array[Int],
                           numberOfOutputNeurons: Int,
                           function: MathematicalFunction,
                           delta: Double,
                           delay: Double) extends Logging {
  /**
   * The perceptron we want to train.
   */
  private var perceptron: Perceptron = new Perceptron(numberOfInputNeurons, hiddenLayers, numberOfOutputNeurons)
  /**
   * The inputs of the perceptron
   */
  var inputValues: IndexedSeq[(Double, Double)] = IndexedSeq.empty
  /**
   * The outputs of the perceptron
   */
  var outputValues: IndexedSeq[(Double, Double)] = IndexedSeq.empty
  /**
   * The abscissa of the first point used as an input value.
   * Should be selected by the user by clicking on the graph.
   */
  var startX: Double = 0.0

  perceptron.initPerceptron()
  setPoints()

  /**
   * Defines the points used to train the neural network, and stores them in the input vector.
   * Obtains the points estimated by the neural network, and puts them in the output vector.
   */
  def setPoints(): Unit = {
    var inputTemp: Vector[Double] = Vector.empty
    var outputTemp: Vector[Double] = Vector.empty
    var x: Double = 0.0

    inputValues = IndexedSeq.empty
    outputValues = IndexedSeq.empty
    info("Length: " + ((numberOfInputNeurons + numberOfOutputNeurons - 2) * delta + delay))

    for (i <- 0 until numberOfInputNeurons) {
      x = startX + delta * i
      inputValues = inputValues :+(x, function(x))
      inputTemp = inputTemp :+ function(x)
    }

    assert(perceptron != null, "Perceptron not instantiated.")
    perceptron.initInputs(inputTemp)
    perceptron.propagate()
    outputTemp = perceptron.getOutput
    for (i <- 0 until numberOfOutputNeurons) {
      x = startX + delay + delta * (i + numberOfInputNeurons - 1)
      if (outputTemp.length > i)
        outputValues = outputValues :+(x, outputTemp(i))
      else {
        outputValues = outputValues :+(x, function(x))
        info("No element number " + i + " in the output.")
      }
    }
  }

  /**
   * Clears the points used as input and output for the training.
   */
  def clearPoints(): Unit = {
    inputValues = Vector.empty
    outputValues = Vector.empty
  }

  def setSamples(): Unit = {
    val length: Double = (numberOfInputNeurons + numberOfOutputNeurons - 2) * delta + delay

    var in: Vector[Double] = Vector.empty[Double]
    var out: Vector[Double] = Vector.empty[Double]
    var start: Double = 0.0
    var x: Double = 0.0

    perceptron.removeSamples()
    for (j <- 0 until 100) {
      in = Vector.empty[Double]
      out = Vector.empty[Double]
      start = j * (1.0 - length) / 100
      for (i <- 0 until numberOfInputNeurons) {
        x = start + delta * i
        in = in :+ function(x)
      }
      for (i <- 0 until numberOfOutputNeurons) {
        x = start + delay + delta * (i + numberOfInputNeurons - 1)
        out = out :+ function(x)
      }
      perceptron.addSample(in, out)
    }
    perceptron.printSamples()
  }

  def learn(): Unit = {
    perceptron.learn(1)
    info("learn - Perceptron error level: " + perceptron.currentError)
  }

  def test(): Unit = {
    perceptron.test
    info("test  - Perceptron error level: " + perceptron.currentError)
  }
}
