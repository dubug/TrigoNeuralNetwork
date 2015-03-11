package model.neuralnetwork

import grizzled.slf4j.Logging

import scala.util.Random

/**
 * Represents a synapse of the neural network. Its weight is initialised with a random value.
 *
 * Created by Yves on 25.02.2015.
 */
object Synapse {
  var random: Random = new Random()
}

class Synapse extends Logging {
  var weight: Double = .0
  var data: Double = .0
  var from: Neuron = null
  var to: Neuron = null

  def this(f: Neuron, t: Neuron) {
    this()
    from = f
    to = t
    weight = Synapse.random.nextDouble / 5.0
    debug("Random:" + weight)
    data = 0.0
    f.outlinks = f.outlinks :+ this
    t.inlinks = t.inlinks :+ this
  }

  def getWeight: Double = {
    weight
  }
}
