package com.test.dixa.calculation

import cats.effect.IO
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import utils.TestUtils

class BrutePrimeCalculatorSpec extends TestUtils {

  behavior of "PrimeCalculators"

  trait mocks {
    val brutePrimeCalculator = BrutePrimeCalculator.build[IO].unsafeRunSync()
  }

  behavior of "BrutePrimeCalculator"

  "calculate" should "produce correct result " in new mocks {

    val result = brutePrimeCalculator.getPrimes(150)
    val expectedResult = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
      89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149)

    result.compile.toList.unsafeRunSync() should contain theSameElementsInOrderAs expectedResult
  }

  "calculate" should "produce correct result with wider interval on the same service" in new mocks {

    val expectedResult1 =
      List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97)
    val expectedResult2 = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
      89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149)

    brutePrimeCalculator
      .getPrimes(150)
      .compile
      .toList
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult2
    brutePrimeCalculator
      .getPrimes(100)
      .compile
      .toList
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult1
  }

  "calculate" should "produce correct result with smaller interval on the same service" in new mocks {

    val expectedResult1 =
      List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97)
    val expectedResult2 = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
      89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149)

    brutePrimeCalculator
      .getPrimes(100)
      .compile
      .toList
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult1
    brutePrimeCalculator
      .getPrimes(150)
      .compile
      .toList
      .unsafeRunSync() should contain theSameElementsInOrderAs expectedResult2
  }

  "calculate" should "return empty list in case of too small borders" in new mocks {

    brutePrimeCalculator.getPrimes(0).compile.toList.unsafeRunSync() shouldBe Nil
    brutePrimeCalculator.getPrimes(-1).compile.toList.unsafeRunSync() shouldBe Nil
  }

  "calculate" should "return 2 with 2 as imput" in new mocks {

    brutePrimeCalculator.getPrimes(2).compile.toList.unsafeRunSync() shouldBe List(2)
  }
}
