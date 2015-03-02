package model.neuralnetwork

/**
 * Created by Yves on 25.02.2015.
 */
class Perceptron {
  var layers: Vector[Layer] = Vector.empty
  var inputSamples: Vector[Vector[Double]] = null
  var outputSamples: Vector[Vector[Double]] = null
  var inputCV: Vector[Vector[Double]] = null
  var outputCV: Vector[Vector[Double]] = null
  var inputLayer: Layer = null
  var outputLayer: Layer = null
  var error: Double = 0.0

  def this(i: Int, o: Int) {
    this()
    inputSamples = Vector.empty
    outputSamples = Vector.empty
    inputCV = Vector.empty
    outputCV = Vector.empty
    inputLayer = new Layer("I", i + 1)
    outputLayer = new Layer("O", o)
    layers = layers :+ inputLayer
    layers = layers :+ outputLayer
    error = 0.0
  }

  def addLayer(n: Int, name: String) {
    layers = layers :+ new Layer(name, n)
  }

  def getLayer(i: Int) = layers(i)

  def connect(sourceLayer: Int, sourceNeuron: Int, destLayer: Int, destNeuron: Int) {
    new Synapse(getLayer(sourceLayer).getNeuron(sourceNeuron), getLayer(destLayer).getNeuron(destNeuron))
  }

  def biasConnect(destLayer: Int, destNeuron: Int) {
    new Synapse(inputLayer.getNeuron(inputLayer.size - 1), getLayer(destLayer).getNeuron(destNeuron))
  }

  def removeSamples {
    inputSamples = Vector.empty
    outputSamples = Vector.empty
  }

  def addSample(i: Vector[Double], o: Vector[Double]) {
    inputSamples = inputSamples :+ i
    outputSamples = outputSamples :+ o
  }

  def printSamples {
    System.out.println(inputSamples + "->" + outputSamples)
  }

  def removeCV {
    inputCV = Vector.empty
    outputCV = Vector.empty
  }

  def addCV(i: Vector[Double], o: Vector[Double]) {
    inputCV = inputCV :+ i
    outputCV = outputCV :+ o
  }

  /**
   * Not used.
   * @param iS
   * @return
   */
  def recognize(iS: Vector[Double]): Vector[Double] = {
    initInputs(iS)
    propagate
    getOutput
  }

  def learn(iterations: Int): Unit = {
    for (i <- 0 to iterations) {
      // accumulate total error over each epoch
      error = 0.0
      for ((iS, oS) <- inputSamples zip outputSamples) {
        learnPattern(iS, oS)
        error += computeError(oS)
      }
    }
  }

  def test {
    error = 0.0
    for ((iS, oS) <- inputCV zip outputCV) {
      initInputs(iS)
      propagate()
      error += computeError(oS)
    }
  }

  def learnPattern(iS: Vector[Double], oS: Vector[Double]) {
    initInputs(iS)
    propagate
    bpAdjustWeights(oS)
  }

  def initInputs(iS: Vector[Double]) {
    var neuron: Neuron = null
    for (iter <- 0 to iS.length)
      inputLayer.getNeuron(iter).output = iS(iter)
    // Bias
    inputLayer.getNeuron(inputLayer.size).output = 1.0;
  }

  def propagate() {
    // Skip the input layer
    for (iter <- 1 to layers.length)
      layers(iter).computeOutputs
  }

  def getOutput: Vector[Double] = {
    for (neuron <- outputLayer.neurons) yield neuron.getOutput
  }

  def computeError(oS: Vector[Double]): Double = {
    var sum: Double = 0.0
    var tmp: Double = 0.0
    for ((neuron, data) <- outputLayer.neurons zip oS) {
      tmp = data - neuron.getOutput
      sum += tmp * tmp
    }
    sum / 2.0
  }

  def currentError: Double = {
    return error
  }

  def bpAdjustWeights(oS: Vector[Double]) {
    outputLayer.computeBackpropDeltas(oS)
    for (iter <- layers.size - 2 to 1) {
      layers(iter).computeBackpropDeltas()
    }
    outputLayer.computeWeights
    for (iter <- layers.size - 2 to 1) {
      layers(iter).computeWeights
    }
  }

  def print {
    layers foreach (_.print)
  }
}
