package com.github.uchibori3.mfcloud.invoice.service

import akka.{ Done, NotUsed }
import akka.actor.ActorSystem
import akka.http.javadsl.model.headers.RawHeader
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.github.uchibori3.mfcloud.invoice.HttpClient
import com.github.uchibori3.mfcloud.invoice.request.CreateBillRequest
import com.github.uchibori3.mfcloud.invoice.response.BillResponse
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.AutoDerivation
import io.circe.syntax._
import com.typesafe.scalalogging.LazyLogging

trait Bills {
  def post(createBillRequest: CreateBillRequest): Source[Either[Throwable, BillResponse], NotUsed]
  def getPdf(id: String): Source[Either[Throwable, HttpResponse], NotUsed]
  def delete(id: String): Source[Either[Throwable, Done], NotUsed]
}

class BillsImpl(
    host: String,
    client: HttpClient,
    credential: OAuth2BearerToken,
    maxConnections: Int
)(implicit system: ActorSystem)
    extends Bills
    with Service
    with FailFastCirceSupport
    with AutoDerivation
    with LazyLogging {
  implicit val executor     = system.dispatcher
  implicit val materializer = ActorMaterializer()

  override def post(createBillRequest: CreateBillRequest): Source[Either[Throwable, BillResponse], NotUsed] = {
    val entity = HttpEntity(`application/json`, createBillRequest.asJson.noSpaces)
    val request = HttpRequest(HttpMethods.POST, "/api/v1/billings", entity = entity)
      .addCredentials(credential)
      .addHeader(RawHeader.create("Accept", "*/*"))

    Source
      .single(request)
      .via(client.connectionHttps(host))
      .map { res =>
        logger.debug(s"Http request: $request")
        logger.debug(s"Http response: $res")
        res
      }
      .mapAsync(maxConnections)(handleError)
      .mapAsync(maxConnections)(Unmarshal(_).to[BillResponse])
      .map(Right.apply)
      .recover {
        case ex =>
          logger.error("Failed", ex)
          Left(ex)
      }
  }

  override def getPdf(id: String): Source[Either[Throwable, HttpResponse], NotUsed] = {
    val request = HttpRequest(HttpMethods.GET, s"/api/v1/billings/$id.pdf")
      .addCredentials(credential)
      .addHeader(RawHeader.create("Accept", "*/*"))

    Source
      .single(request)
      .via(client.connectionHttps(host))
      .map { res =>
        logger.debug(s"Http request: $request")
        logger.debug(s"Http response: $res")
        res
      }
      .mapAsync(maxConnections)(handleError)
      .map(Right.apply)
      .recover {
        case ex =>
          logger.error("Failed", ex)
          Left(ex)
      }
  }

  override def delete(id: String): Source[Either[Throwable, Done], NotUsed] = {
    val request = HttpRequest(HttpMethods.DELETE, s"/api/v1/billings/$id")
      .addCredentials(credential)
      .addHeader(RawHeader.create("Accept", "*/*"))

    Source
      .single(request)
      .via(client.connectionHttps(host))
      .map { res =>
        logger.debug(s"Http request: $request")
        logger.debug(s"Http response: $res")
        res
      }
      .mapAsync(maxConnections)(handleError)
      .map(_ => Right(Done))
      .recover {
        case ex =>
          logger.error("Failed", ex)
          Left(ex)
      }
  }
}
