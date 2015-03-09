package model.neuralnetwork

/**
 * Multi layer perceptron implementation.
 *
 * Created by Yves on 25.02.2015.
 */
class Perceptron(numberInputNeurons: Int, hiddenLayers: Array[Int], numberOutputNeurons: Int) {
  var layers: Vector[Layer] = Vector.empty
  var inputSamples: Vector[Vector[Double]] = Vector.empty
  var outputSamples: Vector[Vector[Double]] = Vector.empty
  var inputCV: Vector[Vector[Double]] = Vector.empty
  var outputCV: Vector[Vector[Double]] = Vector.empty
  var inputLayer: Layer = new Layer("I", numberInputNeurons + 1)
  var outputLayer: Layer = new Layer("O", numberOutputNeurons)
  layers = layers :+ inputLayer
  layers = layers :+ outputLayer
  var error: Double = 0.0

  def initPerceptron(): Unit = {
    var numberOfHiddenLayers: Int = 0

    // Create the hidden layers of neurons
    for (i <- 0 until hiddenLayers.length) {
      if (hiddenLayers(i) != 0) {
        this.addLayer("H" + String.valueOf(i) + "|", hiddenLayers(i))
        numberOfHiddenLayers += 1
      }
    }
    /*
     The bias is an extra input neuron used as a constant
     by all neurons from all layers except the input layer.
     It allows "shifting" the activation function to the
     left or to the right for these neurons.
     See https://stackoverflow.com/questions/2480650/role-of-bias-in-neural-networks .
      */
    for (j <- 0 to numberOfHiddenLayers) {
      for (i <- 0 to hiddenLayers(j)) {
        this.biasConnect(j + 1, i)
      }
      // For the output layer
      this.biasConnect(numberOfHiddenLayers + 1, 0)
    }

    if (numberOfHiddenLayers == 0) {
      // Connect the input layer to the output layer directly
      for (i <- 0 to numberInputNeurons)
        for (j <- 0 to numberOutputNeurons)
          this.connect(0, i, 1, j)
    } else {
      // connect the inputs to the first hidden layer
      for (i <- 0 to hiddenLayers(0))
        for (j <- 0 to numberInputNeurons)
          this.connect(0, j, 1, i)
      // connect the hidden layers together
      for (k <- 0 until numberOfHiddenLayers)
        for (i <- 0 to hiddenLayers(k))
          for (j <- 0 to hiddenLayers(k + 1))
            this.connect(k + 1, i, k + 2, j);
      // connect the last hidden layer to the output
      for (i <- 0 until hiddenLayers(numberOfHiddenLayers))
        for (j <- 0 to numberOutputNeurons)
          this.connect(numberOfHiddenLayers, i, numberOfHiddenLayers + 1, j)
    }
  }

  def addLayer(name: String, numberOfNeurons: Int) {
    layers = layers :+ new Layer(name, numberOfNeurons)
  }

  def getLayer(i: Int) = layers(i)

  /**
   * Connects any given two neurons with a Synapse.
   * @param sourceLayer id of the source layer
   * @param sourceNeuron id of the neuron within sourceLayer
   * @param destLayer id of the destination layer
   * @param destNeuron id of the neuron within destLayer
   */
  def connect(sourceLayer: Int, sourceNeuron: Int, destLayer: Int, destNeuron: Int) {
    new Synapse(getLayer(sourceLayer).getNeuron(sourceNeuron), getLayer(destLayer).getNeuron(destNeuron))
  }

  /**
   * Connects the last neuron of the input layer to the specified
   * neuron of the specified layer, with a Synapse object.
   * @param destLayer id of the layer
   * @param destNeuron id of the neuron within destLayer
   */
  def biasConnect(destLayer: Int, destNeuron: Int) {
    new Synapse(inputLayer.getNeuron(inputLayer.size - 1), getLayer(destLayer).getNeuron(destNeuron))
  }

  def removeSamples() {
    inputSamples = Vector.empty
    outputSamples = Vector.empty
  }

  def addSample(i: Vector[Double], o: Vector[Double]) {
    inputSamples = inputSamples :+ i
    outputSamples = outputSamples :+ o
  }

  def printSamples() {
    System.out.println(inputSamples + "->" + outputSamples)
  }

  def removeCV() {
    inputCV = Vector.empty
    outputCV = Vector.empty
  }

  def addCV(i: Vector[Double], o: Vector[Double]) {
    inputCV = inputCV :+ i
    outputCV = outputCV :+ o
  }

  /**
   * Not used.
   * @param iS the input as a Vector of Double
   * @return
   */
  def recognize(iS: Vector[Double]): Vector[Double] = {
    initInputs(iS)
    propagate()
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

  def test() {
    error = 0.0
    for ((iS, oS) <- inputCV zip outputCV) {
      initInputs(iS)
      propagate()
      error += computeError(oS)
    }
  }

  def learnPattern(iS: Vector[Double], oS: Vector[Double]) {
    initInputs(iS)
    propagate()
    bpAdjustWeights(oS)
  }

  def initInputs(iS: Vector[Double]) {
    var neuron: Neuron = null
    for (iter <- 0 to iS.length)
      inputLayer.getNeuron(iter).output = iS(iter)
    // Bias
    inputLayer.getNeuron(inputLayer.size).output = 1.0
  }

  /**
   * Propagates the input signal through all layers consecutively.
   */
  def propagate() {
    // Skip the input layer
    for (iter <- 1 to layers.length)
      layers(iter).computeOutputs()
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

  def currentError: Double = error

  /**
   * Adjusts weights according to the backpropagation algorithm.
   * @param oS the desired output of the perceptron
   */
  def bpAdjustWeights(oS: Vector[Double]) {
    outputLayer.computeBackpropDeltas(oS)
    for (iter <- layers.size - 2 to 1) {
      layers(iter).computeBackpropDeltas()
    }
    outputLayer.computeWeights()
    for (iter <- layers.size - 2 to 1) {
      layers(iter).computeWeights()
    }
  }

  def print() {
    layers foreach (_.print())
  }
}
