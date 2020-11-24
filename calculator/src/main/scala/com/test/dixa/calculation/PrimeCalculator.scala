package com.test.dixa.calculation

import fs2.{ Stream => FStream }

trait PrimeCalculator[F[_]] {
  def getPrimes(border: Int): FStream[F, Int]
}
