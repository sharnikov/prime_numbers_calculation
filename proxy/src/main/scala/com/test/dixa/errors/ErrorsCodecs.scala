package com.test.dixa.errors

import com.test.dixa.errors.Errors.{ CalculationFailedException, ExternalServerUnavailableException, InternalException }

object ErrorsCodecs {
  import io.circe._
  import io.circe.generic.semiauto._

  implicit val externalServerUnavailableExceptionEncoder: Codec[ExternalServerUnavailableException] =
    deriveCodec[ExternalServerUnavailableException]
  implicit val calculationFailedException: Codec[CalculationFailedException] = deriveCodec[CalculationFailedException]
  implicit val internalException: Codec[InternalException]                   = deriveCodec[InternalException]
}
