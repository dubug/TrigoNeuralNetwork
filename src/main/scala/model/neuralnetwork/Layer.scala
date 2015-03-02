package model.neuralnetwork

/**
 * A layer of neurons in a neural network.
 *
 * Created by Yves on 25.02.2015.
 */
class Layer {
  var neurons: Vector[Neuron] = Vector.empty[Neuron]
  var size: Int = 0

  def this(l: String, s: Int) {
    this()
    var label: String = null
    size = s
    for (i <- 0 to s) {
      label = l + String.valueOf(i)
      neurons :+ new Neuron(label)
    }
  }

  def getNeuron(i: Int): Neuron = {
    neurons(i)
  }

  def computeOutputs() {
    neurons foreach (_.computeOutput)
  }

  /**
   * Computes backpropagation deltas for output neurons.
   *
   * @param inputs a vector of Doubles, one per neuron in the layer
   */
  def computeBackpropDeltas(inputs: Vector[Double]) {
    for ((neuron, input) <- neurons zip inputs) neuron.computeBackpropDelta(input)
  }

  /**
   * Computes backpropagation deltas for neurons other than the output neurons.
   */
  def computeBackpropDeltas() {
    for (neuron <- neurons) neuron.computeBackpropDelta
  }

  def computeWeights() {
    for (neuron <- neurons) neuron.computeWeight
  }

  def print() {
    for (neuron <- neurons) neuron.print
  }
}

