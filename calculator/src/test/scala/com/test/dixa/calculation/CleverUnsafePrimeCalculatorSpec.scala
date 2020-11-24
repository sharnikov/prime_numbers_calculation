package com.test.dixa.calculation

import cats.effect.IO
import utils.TestUtils

class CleverUnsafePrimeCalculatorSpec extends TestUtils {

  behavior of "PrimeCalculators"

  trait mocks {
    val cleverPrimeCalculator = CleverUnsafePrimeCalculator.build[IO].unsafeRunSync()
  }

  behavior of "BrutePrimeCalculator"

  "calculate" should "produce correct result " in new mocks {

    val result = cleverPrimeCalculator.getPrimes(150)
    val expectedResult = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
      89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149)

    result
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult
  }

  "calculate" should "produce correct result with wider interval on the same service" in new mocks {

    val expectedResult1 =
      List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97)
    val expectedResult2 = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
      89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149)

    cleverPrimeCalculator
      .getPrimes(150)
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult2
    cleverPrimeCalculator
      .getPrimes(100)
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult1
  }

  "calculate" should "produce correct result with smaller interval on the same service" in new mocks {

    val expectedResult1 =
      List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97)
    val expectedResult2 = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
      89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149)

    cleverPrimeCalculator
      .getPrimes(100)
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult1
    cleverPrimeCalculator
      .getPrimes(150)
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult2
  }

  "calculate" should "return empty list in case of too small borders" in new mocks {

    cleverPrimeCalculator.getPrimes(0).unsafeRunSync() shouldBe Nil
    cleverPrimeCalculator.getPrimes(-1).unsafeRunSync() shouldBe Nil
  }

  "calculate" should "return 2 with 2 as iput" in new mocks {

    cleverPrimeCalculator.getPrimes(2).unsafeRunSync() shouldBe List(2)
  }
}
