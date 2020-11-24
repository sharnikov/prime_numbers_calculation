package com.test.dixa.calculation

import cats.effect.Sync

object BrutePrimeCalculator {
  def build[F[_]: Sync]: F[PrimeCalculator[F]] =
    Sync[F].delay(new BrutePrimeCalculator[F])
}

class BrutePrimeCalculator[F[_]: Sync] private () extends PrimeCalculator[F] {

  private val primesStream = 2 #:: getNextPrime(3)

  def getPrimes(border: Int): F[List[Int]] =
    Sync[F].delay {
      val result = primesStream.takeWhile(_ <= border).toList
      println(s"Calculated $result")
      result
    }

  private def getNextPrime(current: Int): LazyList[Int] =
    if (isPrime(current)) {
      current #:: getNextPrime(current + 1)
    } else {
      getNextPrime(current + 1)
    }

  private def isPrime(number: Int): Boolean =
    (2 until number / 2 + 1).forall(element => number % element != 0)

}
