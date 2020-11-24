package com.test.dixa.calculation

import cats.effect.Sync
import fs2.{ Stream => FStream }

object BrutePrimeCalculator {
  def build[F[_]: Sync]: F[PrimeCalculator[F]] =
    Sync[F].delay(new BrutePrimeCalculator[F])
}

class BrutePrimeCalculator[F[_]: Sync] private () extends PrimeCalculator[F] {

  private val fsPrimesStream = FStream.emit(2) ++ getNextPrime(3)

  def getPrimes(border: Int): FStream[F, Int] =
    fsPrimesStream.takeWhile(_ <= border)

  private def getNextPrime(current: Int): FStream[F, Int] =
    if (isPrime(current)) {
      FStream.emit(current) ++ getNextPrime(current + 1)
    } else {
      getNextPrime(current + 1)
    }

  private def isPrime(number: Int): Boolean =
    (2 until number / 2 + 1).forall(element => number % element != 0)

}
