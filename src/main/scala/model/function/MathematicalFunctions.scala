package model.function

import model.neuralnetwork.Perceptron

/**
 * Created by Yves on 25.02.2015.
 *
 * Replaces FunctionCanvas from the NN example and UnitConverter from the ScalaFXML example
 */
class MathematicalFunctions(mathematicalFunctions: MathematicalFunction*) {
  val available = List(mathematicalFunctions: _*)
  var input = Vector.empty[Double]
  var output = Vector.empty[Double]
  var n_in: Int = 0
  var n_out: Int = 0
  var delay: Double = .0
  var delta: Double = .0
  var startx: Double = 0.0
  var perceptron: Perceptron = null
  var function = Cosine
}

trait MathematicalFunction {
  val description: String

  def run(x: String): String

  override def toString = description
}

object Sine extends MathematicalFunction {
  val description: String = "Sine"

  def run(x: String): String = try {
    (0.5 + Math.sin(x.toDouble * 4 * Math.PI) / 2).toString
  } catch {
    case ex: Throwable => ex.toString
  }
}

object Cosine extends MathematicalFunction {
  val description: String = "Cosine"

  def run(x: String): String = try {
    (0.5 + Math.cos(x.toDouble * Math.PI) / 2).toString
  } catch {
    case ex: Throwable => ex.toString
  }
}

object Complex extends MathematicalFunction {
  val description: String = "Complex"

  def run(x: String): String = try {
    (0.5 + (Math.sin(x.toDouble * 3 * Math.PI) + Math.sin(x.toDouble * 7 * Math.PI) + Math.sin(x.toDouble * 8 * Math.PI) + Math.sin(x.toDouble * 11 * Math.PI)) / 8).toString
  } catch {
    case ex: Throwable => ex.toString
  }
}

object Chaos extends MathematicalFunction {
  val description: String = "Chaos"
  var Ikeda: Array[Double] = initIkeda()

  def run(x: String): String = try {
    var value: Double = 0
    var i: Int = 0
    var dx: Double = .0
    if (x.toDouble >= 2.00) value = Ikeda(200)
    else {
      i = (x.toDouble * 100.0).toInt
      dx = 100 * x.toDouble - i.toDouble
      value = Ikeda(i) * (1 - dx) + Ikeda(i + 1) * dx
    }
    value.toString
  } catch {
    case ex: Throwable => ex.toString
  }

  def initIkeda(): Array[Double] = {
    var i: Int = 0
    var x1: Double = .0
    var x2: Double = .0
    var theta: Double = .0
    x1 = 0.0
    x2 = 0.0
    var ikeda = new Array[Double](201)

    for (i <- 0 to 200) {
      theta = 0.4 - 6.0 / (1 + x1 * x1 + x2 * x2)
      x1 = 1.0 + 0.7 * (x1 * Math.cos(theta) - x2 * Math.sin(theta))
      x2 = 0.7 * (x1 * Math.sin(theta) + x2 * Math.sin(theta))
      ikeda(i) = x1 - 0.2
    }
    return ikeda
  }
}