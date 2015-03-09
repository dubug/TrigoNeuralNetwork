package model.neuralnetwork

object Neuron {
  var momentum: Double = 0.9
  var learningRate: Double = 0.2
}

class Neuron {
  var output: Double = 0.0
  var sum: Double = 0.0
  var delta: Double = 0.0
  var inlinks: Vector[Synapse] = Vector.empty[Synapse]
  var outlinks: Vector[Synapse] = Vector.empty[Synapse]
  var label: String = null

  def this(l: String) {
    this()
    label = l
  }

  def getOutput: Double = {
    output
  }

  def getDelta: Double = {
    delta
  }

  def computeOutput {
    sum = 0.0
    inlinks.foreach((synapse: Synapse) => sum += synapse.from.getOutput * synapse.getWeight)
    // Sigmoid function
    output = 1.0 / (1.0 + Math.exp(-sum))
  }

  /**
   * Computes the backpropagation delta for an output neuron.
   * @param d
   */
  def computeBackpropDelta(d: Double) {
    delta = (d - output) * output * (1.0 - output)
  }

  /**
   * Computes the backpropagation delta for a neuron in a hidden layer.
   */
  def computeBackpropDelta {
    var errorSum: Double = 0.0

    outlinks.foreach((synapse: Synapse) => errorSum += synapse.to.delta * synapse.getWeight)
    delta = output * (1.0 - output) * errorSum
  }

  def computeWeight {
    inlinks.foreach { (synapse: Synapse) =>
      synapse.data = Neuron.learningRate * delta * synapse.from.getOutput + Neuron.momentum * synapse.data
      synapse.weight += synapse.data
    }
  }

  override def toString: String = {
    var me: String = label + "=" + output + ": "
    outlinks.foreach((synapse: Synapse) =>
      me += synapse.to.label + "(" + synapse.weight + ") "
    )
    me
  }
}
