package com.test.dixa.calculation

import scala.collection.mutable

class CleverUnsafePrimeCalculator extends PrimeCalculator {

  private var currentLast = 1
  private var currentPrimes = List(1)

  override def getPrimes(border: Int): List[Int] = {
    if (currentLast < border) {
      addMore(border: Int)
    }

    currentPrimes.takeWhile(_ <= border)
  }

  def addMore(border: Int): List[Int] = {
    val primes = mutable.ArrayBuffer.fill(border + 1)(true)

    (2 to scala.math.sqrt(border).toInt).foreach { number =>
      if (primes(number)) {
        (2 * number to border by number).foreach { notPrime =>
          primes(notPrime) = false
        }
      }
    }

    primes.zipWithIndex.filter(_._1).map(_._2).toList
  }
}
