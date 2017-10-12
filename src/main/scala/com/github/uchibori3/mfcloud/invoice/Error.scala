package com.github.uchibori3.mfcloud.invoice

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.github.uchibori3.mfcloud.invoice.utils.JsonUnmarshaller
import io.circe.Decoder

import scala.concurrent.{ ExecutionContext, Future }

object Error {
  class MfcloudException(val code: String, val description: String, val response: HttpResponse)
      extends RuntimeException(s"$code: $description")

  object MfcloudException extends JsonUnmarshaller {
    case class MfcloudErrorResponse(code: String, errors: Seq[MfcloudErrorMessage])
    case class MfcloudErrorMessage(message: String)

    implicit def errorResponseDecoder: Decoder[MfcloudErrorResponse] = Decoder.instance { c =>
      for {
        code   <- c.downField("code").as[String].right
        errors <- c.downField("errors").as[Seq[MfcloudErrorMessage]].right
      } yield MfcloudErrorResponse(code, errors)
    }

    implicit def errorMessageDecoder: Decoder[MfcloudErrorMessage] = Decoder.instance { c =>
      for {
        message <- c.downField("message").as[String].right
      } yield MfcloudErrorMessage(message)
    }

    def fromHttpResponse(response: HttpResponse)(implicit ec: ExecutionContext,
                                                 mat: Materializer): Future[MfcloudException] = {
      Unmarshal(response).to[MfcloudErrorResponse].map { r =>
        new MfcloudException(r.code, r.errors.mkString("[", ", ", "]"), response)
      }
    }
  }
}
