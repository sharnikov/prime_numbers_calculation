package com.test.dixa.services

import cats.effect.{ContextShift, Sync}
import fs2.{Stream => FStream}

import io.chrisdavenport.log4cats.Logger

object RpcCalculationService {
  def build[F[_]: Sync: ContextShift: Logger]: F[RpcCalculationService[F]] =
    Sync[F].delay(new RpcCalculationService[F])
}

trait CalculationService[F[_]] {
  def getPrimeStream(goalNumber: Int): FStream[F, Int]
}

class RpcCalculationService[F[_]: Sync: ContextShift: Logger] private() extends CalculationService[F] {
  override def getPrimeStream(goalNumber: Int): FStream[F, Int] =
    FStream.range(0, 100000)
}
