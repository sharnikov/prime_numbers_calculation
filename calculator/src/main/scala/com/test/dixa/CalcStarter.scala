package com.test.dixa


import java.util.concurrent.TimeUnit

import org.http4s.HttpService
import java.util.logging.Logger

import dixa.primes.CalculatorGrpc
import io.grpc.netty.NettyServerBuilder
import io.grpc.{Server, ServerBuilder}

import scala.concurrent.ExecutionContext

object CalcStarter extends App {

  val server = NettyServerBuilder
    .forPort(9999)
    .keepAliveTime(500, TimeUnit.SECONDS)
    .addService(CalculatorGrpc.bindService(new CalculationService, ExecutionContext.global)).build().start()
  server.awaitTermination()

}
