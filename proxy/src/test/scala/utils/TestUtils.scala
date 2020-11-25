package utils

import cats.effect.IO
import com.test.dixa.errors.Errors.{ AppException, CalculationFailedException, ExternalServerUnavailableException }
import fs2.{ Stream => FStream }
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.reflect.ClassTag

trait TestUtils extends AnyFlatSpec with Matchers with MockFactory {

  def awaitFailedStream[T <: AppException](value: FStream[IO, _], expectedMessage: String)(implicit tag: ClassTag[T]) =
    try {
      val result = value.compile.toList.unsafeRunSync()
      fail("Result is expected to fail, but it succeed with value: " + result)
    } catch {
      case exception: T =>
        exception.message shouldBe expectedMessage
      case exception =>
        fail(s"Wrong type of exception received: $exception")
    }

}
