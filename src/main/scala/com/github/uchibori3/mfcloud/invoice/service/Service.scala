package com.github.uchibori3.mfcloud.invoice.service

import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer
import com.github.uchibori3.mfcloud.invoice.Error.MfcloudException

import scala.concurrent.{ ExecutionContext, Future }

trait Service {
  def handleError(response: HttpResponse)(implicit ec: ExecutionContext, mat: Materializer): Future[HttpResponse] = {
    if (response.status.isFailure()) MfcloudException.fromHttpResponse(response).flatMap(Future.failed(_))
    else Future.successful(response)
  }
}
