package util

/**
 * Utility functions related to mathematics.
 *
 * Created by Yves on 06.03.2015.
 */
object Converters {
  def stringToPositiveDouble(input: String): Double = {
    var output: Double = 0.0
    try {
      output = input.toDouble
    } catch {
      case _: Exception => output = 0.0
    }
    if (output < 0.0) output = 0.0
    output
  }

  def stringToPositiveInt(input: String): Int = {
    var output: Int = 0
    try {
      output = input.toInt
    } catch {
      case _: Exception => output = 0
    }
    if (output < 0) output = 0
    output
  }
}
