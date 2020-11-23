package com.test.dixa.calculation

trait PrimeCalculator[F[_]] {
  def getPrimes(border: Int): F[List[Int]]
}
