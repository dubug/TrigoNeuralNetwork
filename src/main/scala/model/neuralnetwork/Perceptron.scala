package model.neuralnetwork

/**
 * Multi layer perceptron implementation.
 *
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

/*
  def initPerceptron {
    var hid= new Array[Int](3)
    var nLayer: Int = 0
    var i: Int = 0
    var j: Int = 0
    var k: Int = 0
    var text: String = null
    nLayer = 0
    {
      i = 0
      while (i < 3) {
        {
          text = hiddenTF(i).getText
          if ("" == text) hid(i) = 0
          else hid(i) = (Integer.valueOf(text)).intValue
          if (hid(i) != 0) {
            val s: String = "H" + String.valueOf(i) + "|"
            addLayer(hid(i), s)
            nLayer += 1
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    {
      j = 0
      while (j < nLayer) {
        {
          i = 0
          while (i < hid(j)) {
            biasConnect(j + 1, i)
            ({
              i += 1; i - 1
            })
          }
        }
        ({
          j += 1; j - 1
        })
      }
    }
    biasConnect(nLayer + 1, 0)
    if (nLayer == 0) {
      i = 0
      while (i < n_in) {
        {
          j = 0
          while (j < n_out) {
            connect(0, i, 1, j)
            ({
              j += 1; j - 1
            })
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    else {
      {
        i = 0
        while (i < hid(0)) {
          {
            j = 0
            while (j < n_in) {
              connect(0, j, 1, i)
              ({
                j += 1; j - 1
              })
            }
          }
          ({
            i += 1; i - 1
          })
        }
      }
      {
        k = 0
        while (k < nLayer - 1) {
          {
            i = 0
            while (i < hid(k)) {
              {
                j = 0
                while (j < hid(k + 1)) {
                  connect(k + 1, i, k + 2, j)
                  ({
                    j += 1; j - 1
                  })
                }
              }
              ({
                i += 1; i - 1
              })
            }
          }
          ({
            k += 1; k - 1
          })
        }
      }
      {
        i = 0
        while (i < hid(nLayer - 1)) {
          {
            j = 0
            while (j < n_out) {
              connect(nLayer, i, nLayer + 1, j)
              ({
                j += 1; j - 1
              })
            }
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    functionCanvas.setPerceptron(perceptron)
  }
*/

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
   * @param iS
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
