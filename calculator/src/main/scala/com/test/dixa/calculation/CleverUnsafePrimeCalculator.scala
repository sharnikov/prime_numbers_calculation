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

  private var currentLast   = 1
  private var currentPrimes = List(1)

  override def getPrimes(border: Int): F[List[Int]] = {
    if (currentLast < border) {
      currentPrimes = addMore(border: Int)
    }

    currentPrimes.takeWhile(_ <= border).pure[F]
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

    primes.zipWithIndex.filter(_._1).map(_._2).drop(2).toList
  }
}
