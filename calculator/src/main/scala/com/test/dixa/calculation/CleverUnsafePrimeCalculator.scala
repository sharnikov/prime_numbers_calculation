package com.test.dixa.calculation

import cats.Applicative
import cats.effect.Sync
import fs2.{ Stream => FStream }

import scala.collection.mutable

object CleverUnsafePrimeCalculator {
  def build[F[_]: Sync]: F[CleverUnsafePrimeCalculator[F]] =
    Sync[F].delay(new CleverUnsafePrimeCalculator[F])
}

class CleverUnsafePrimeCalculator[F[_]: Applicative] private () extends PrimeCalculator[F] {

  private var currentLast   = 2
  private var currentPrimes = List(2)

  override def getPrimes(border: Int): FStream[F, Int] =
    if (border <= 1) FStream.emits(List.empty[Int]).covary[F]
    else {
      if (currentLast < border) {
        currentPrimes ++= addMore(currentLast, border)
        currentLast = border
        FStream.emits(currentPrimes).covary[F]
      } else {
        FStream.emits(currentPrimes.takeWhile(_ <= border)).covary[F]
      }

    }

  def addMore(currentLast: Int, border: Int): List[Int] = {
    val primes = mutable.ArrayBuffer.fill(border + 1)(true)

    (2 to scala.math.sqrt(border).toInt).foreach { number =>
      if (primes(number)) {
        (2 * number to border by number).foreach { notPrime =>
          primes(notPrime) = false
        }
      }
    }

    primes.zipWithIndex.collect { case (isPrime, value) if isPrime && value > currentLast => value }.toList
  }
}
