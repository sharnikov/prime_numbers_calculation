package com.test.dixa.calculation

class BrutePrimeCalculator extends PrimeCalculator {

  val primesStream = 2 #:: getNextPrime(3)

  def getPrimes(border: Int): List[Int] = {
    primesStream.takeWhile(_ <= border).toList
  }

  private def getNextPrime(current: Int): LazyList[Int] = {
    if (isPrime(current)) {
      current #:: getNextPrime(current + 1)
    } else {
      getNextPrime(current + 1)
    }
  }

  private def isPrime(number: Int): Boolean = {
    (2 until number).forall(element => number % element != 0)
  }

}
