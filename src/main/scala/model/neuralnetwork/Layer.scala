package model.neuralnetwork

import grizzled.slf4j.Logging

/**
 * A layer of neurons in a neural network.
 *
 * Created by Yves on 25.02.2015.
 */
class Layer extends Logging {
  var label: String = "No label"
  var neurons: Vector[Neuron] = Vector.empty[Neuron]
  var size: Int = 0

  def this(label: String, numberOfNeurons: Int) {
    this()
    var newNeuron: Neuron = null
    size = numberOfNeurons
    this.label = label
    for (i <- 0 until size)
      neurons=neurons :+ new Neuron(label + i)
    debug("Created Layer with label " + label + " containing " + neurons.length + " Neuron object(s).")
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

  override def toString: String = {
    var me: String = ""
    for (neuron <- neurons) me += neuron.toString + "\n"
    me
  }
}

