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
import com.typesafe.scalalogging.LazyLogging
import com.github.uchibori3.mfcloud.invoice.HttpClient
import com.github.uchibori3.mfcloud.invoice.request.{ CreatePartnerRequest, UpdatePartnerRequest }
import com.github.uchibori3.mfcloud.invoice.response.PartnerResponse
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.AutoDerivation
import io.circe.syntax._

import scala.concurrent.ExecutionContextExecutor

trait Partners {
  def get(id: String): Source[Either[Throwable, PartnerResponse], NotUsed]
  def post(createPartnerRequest: CreatePartnerRequest): Source[Either[Throwable, PartnerResponse], NotUsed]
  def update(id: String,
             updatePartnerRequest: UpdatePartnerRequest): Source[Either[Throwable, PartnerResponse], NotUsed]
  def delete(id: String): Source[Either[Throwable, Done], NotUsed]
}

class PartnersImpl(
    host: String,
    client: HttpClient,
    credential: OAuth2BearerToken,
    maxConnections: Int
)(implicit system: ActorSystem)
    extends Partners
    with Service
    with FailFastCirceSupport
    with AutoDerivation
    with LazyLogging {
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer    = ActorMaterializer()

  override def get(id: String): Source[Either[Throwable, PartnerResponse], NotUsed] = {
    val request = HttpRequest(HttpMethods.GET, s"/api/v1/partners/$id.json")
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
      .mapAsync(maxConnections)(Unmarshal(_).to[PartnerResponse])
      .map(Right.apply)
      .recover {
        case ex =>
          logger.error("Failed", ex)
          Left(ex)
      }
  }

  override def post(createPartnerRequest: CreatePartnerRequest): Source[Either[Throwable, PartnerResponse], NotUsed] = {
    val entity = HttpEntity(`application/json`, createPartnerRequest.asJson.noSpaces)
    val request = HttpRequest(HttpMethods.POST, "/api/v1/partners", entity = entity)
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
      .mapAsync(maxConnections)(Unmarshal(_).to[PartnerResponse])
      .map(Right.apply)
      .recover {
        case ex =>
          logger.error("Failed", ex)
          Left(ex)
      }
  }

  override def update(
      id: String,
      updatePartnerRequest: UpdatePartnerRequest
  ): Source[Either[Throwable, PartnerResponse], NotUsed] = {
    val entity = HttpEntity(`application/json`, updatePartnerRequest.asJson.noSpaces)
    val request = HttpRequest(HttpMethods.PUT, "/api/v1/partners", entity = entity)
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
      .mapAsync(maxConnections)(Unmarshal(_).to[PartnerResponse])
      .map(Right.apply)
      .recover {
        case ex =>
          logger.error("Failed", ex)
          Left(ex)
      }
  }

  override def delete(id: String): Source[Either[Throwable, Done], NotUsed] = {
    val request = HttpRequest(HttpMethods.DELETE, s"/api/v1/partners/$id")
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
