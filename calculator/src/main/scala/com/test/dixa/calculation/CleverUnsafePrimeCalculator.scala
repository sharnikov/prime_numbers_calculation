package com.test.dixa.calculation

import cats.Applicative
import cats.effect.Sync
import cats.syntax.applicative._

import scala.collection.mutable

object CleverUnsafePrimeCalculator {
  def build[F[_]: Sync]: F[CleverUnsafePrimeCalculator[F]] =
    Sync[F].delay(new CleverUnsafePrimeCalculator[F])
}

class CleverUnsafePrimeCalculator[F[_]: Applicative] private () extends PrimeCalculator[F] {

  private var currentLast   = 2
  private var currentPrimes = List(2)

  override def getPrimes(border: Int): F[List[Int]] =
    if (border <= 1) List.empty[Int].pure[F]
    else {
      if (currentLast < border) {
        currentPrimes ++= addMore(currentLast, border)
        currentLast = border
        currentPrimes.pure[F]
      } else {
        currentPrimes.takeWhile(_ <= border).pure[F]
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

    primes.zipWithIndex
      .dropWhile { case (_, index) => index <= currentLast }
      .collect { case (isPrime, value) if isPrime => value }
      .toList
  }
}
