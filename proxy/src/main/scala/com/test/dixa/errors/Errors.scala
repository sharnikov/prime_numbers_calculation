package com.test.dixa.errors

import sttp.model.StatusCode
import sttp.tapir.{ oneOf, statusDefaultMapping, statusMapping }
import sttp.tapir.json.circe._

object Errors {

  import com.test.dixa.errors.ErrorsCodecs._

  case class ExternalServerUnavailableException(message: String) extends Throwable
  case class CalculationFailedException(message: String)         extends Throwable
  case class InternalException(message: String)                  extends Throwable

  val defaultErrors = oneOf[Throwable](
    statusMapping(StatusCode.ServiceUnavailable, jsonBody[ExternalServerUnavailableException]),
    statusMapping(StatusCode.InternalServerError, jsonBody[CalculationFailedException]),
    statusMapping(StatusCode.InternalServerError, jsonBody[InternalException]),
    statusDefaultMapping(jsonBody[InternalException].description("Unexpected error"))
  )

}
