package com.test.dixa.calculation

import cats.effect.IO
import utils.TestUtils

class BrutePrimeCalculatorSpec extends TestUtils {

  behavior of "PrimeCalculators"

  trait mocks {
    val brutePrimeCalculator = BrutePrimeCalculator.build[IO].unsafeRunSync()
  }

  behavior of "BrutePrimeCalculator"

  "calculate" should "numbers correctly " in new mocks {

    val result = brutePrimeCalculator.getPrimes(150)
    val expectedResult = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41,
      43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113,
      127, 131, 137, 139, 149)

    result
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult
  }

  "calculate" should "numbers correctly with wider interval on the same service" in new mocks {

    val expectedResult1 = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41,
      43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97)
    val expectedResult2 = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41,
      43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113,
      127, 131, 137, 139, 149)

    brutePrimeCalculator
      .getPrimes(150)
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult2
    brutePrimeCalculator
      .getPrimes(100)
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult1
  }

  "calculate" should "calculate numbers correctly with smaller interval on the same service" in new mocks {

    val expectedResult1 = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41,
      43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97)
    val expectedResult2 = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41,
      43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113,
      127, 131, 137, 139, 149)

    brutePrimeCalculator
      .getPrimes(100)
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult1
    brutePrimeCalculator
      .getPrimes(150)
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult2
  }

}
