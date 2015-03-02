package model.neuralnetwork

import java.util.Random

/**
 * Created by Yves on 25.02.2015.
 */
object Synapse {
  var random: Random = new Random
}

class Synapse {
  var weight: Double = .0
  var data: Double = .0
  var from: Neuron = null
  var to: Neuron = null

  def this(f: Neuron, t: Neuron) {
    this()
    from = f
    to = t
    weight = Synapse.random.nextDouble / 5.0
    data = 0.0
    f.outlinks = f.outlinks :+ this
    t.inlinks = t.inlinks :+ this
  }

  def getWeight: Double = {
    weight
  }
}
