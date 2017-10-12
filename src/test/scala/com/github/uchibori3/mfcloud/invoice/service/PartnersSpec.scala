package com.github.uchibori3.mfcloud.invoice.service

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{ HttpEntity, HttpRequest, HttpResponse, StatusCodes }
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Sink }
import akka.stream.testkit.scaladsl.TestSink
import com.github.uchibori3.mfcloud.invoice.Error.MfcloudException
import com.github.uchibori3.mfcloud.invoice.response.PartnerResponse
import com.github.uchibori3.mfcloud.invoice.HttpClient
import com.github.uchibori3.mfcloud.invoice.testkit.{
  CreatePartnerRequestFixtures,
  PartnerResponseFixtures,
  UpdatePartnerRequestFixtures
}
import io.circe.ParsingFailure
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ BeforeAndAfterAll, DiagrammedAssertions, FlatSpec }
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class PartnersSpec
    extends FlatSpec
    with DiagrammedAssertions
    with BeforeAndAfterAll
    with MockFactory
    with ScalaFutures {
  implicit val system       = ActorSystem("partners")
  implicit val executor     = system.dispatcher
  implicit val materializer = ActorMaterializer()

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), Duration.Inf)
  }

  private val token          = "e60aa4e706e05aa73a3494f0b54f7b8d81e0542897708e2d64703c52cb40af02"
  private val credential     = OAuth2BearerToken(token)
  private val host           = "invoice.moneyforward.com"
  private val maxConnections = 4

  "#get" should "returns partner response" in {
    val partnerId    = "ID"
    val response     = PartnerResponseFixtures.build
    val httpEntity   = HttpEntity(`application/json`, response.asJson.noSpaces)
    val httpResponse = HttpResponse(StatusCodes.OK, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val partners = new PartnersImpl(host, httpClient, credential, maxConnections)

    partners
      .get(partnerId)
      .runWith(TestSink.probe[Either[Throwable, PartnerResponse]])
      .requestNext(Right(response))
      .expectComplete()
  }

  it should "returns ParsingFailure exception when entity unmasshalled" in {
    val partnerId    = "ID"
    val httpEntity   = HttpEntity(`application/json`, "{")
    val httpResponse = HttpResponse(StatusCodes.OK, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val partners = new PartnersImpl(host, httpClient, credential, maxConnections)

    val result = partners.get(partnerId).runWith(Sink.head)
    whenReady(result) { ex =>
      assert(ex.isLeft)
      assert(ex.left.get.isInstanceOf[ParsingFailure])
    }
  }

  it should "returns MfcloudException when not found" in {
    val httpEntity = HttpEntity(
      `application/json`,
      """
        |{
        |  "code" : "404",
        |  "errors" : [
        |    {
        |      "message" : "存在しないIDが渡されました。"
        |    }
        |  ]
        |}
      """.stripMargin
    )
    val httpResponse = HttpResponse(StatusCodes.NotFound, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val partners = new PartnersImpl(host, httpClient, credential, maxConnections)

    val result = partners.get("ID").runWith(Sink.head)

    whenReady(result) { ex =>
      assert(ex.isLeft)
      assert(ex.left.get.isInstanceOf[MfcloudException])
    }
  }

  "#post" should "returns partner response" in {
    val request      = CreatePartnerRequestFixtures.build
    val response     = PartnerResponseFixtures.build
    val httpEntity   = HttpEntity(`application/json`, response.asJson.noSpaces)
    val httpResponse = HttpResponse(StatusCodes.Created, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val partners = new PartnersImpl(host, httpClient, credential, maxConnections)

    partners
      .post(request)
      .runWith(TestSink.probe[Either[Throwable, PartnerResponse]])
      .requestNext(Right(response))
      .expectComplete()
  }

  it should "returns ParsingFailure exception when entity unmarshalled" in {
    val request      = CreatePartnerRequestFixtures.build
    val httpEntity   = HttpEntity(`application/json`, "{")
    val httpResponse = HttpResponse(StatusCodes.Created, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val partners = new PartnersImpl(host, httpClient, credential, maxConnections)

    val result = partners.post(request).runWith(Sink.head)

    whenReady(result) { ex =>
      assert(ex.isLeft)
      assert(ex.left.get.isInstanceOf[ParsingFailure])
    }
  }

  it should "returns MfcloudException when bad request" in {
    val request = CreatePartnerRequestFixtures.build
    val httpEntity = HttpEntity(
      `application/json`,
      """
        |{
        |  "code" : "400",
        |  "errors" : [
        |    {
        |      "message" : "不正な都道府県名が渡されました。"
        |    }
        |  ]
        |}
      """.stripMargin
    )
    val httpResponse = HttpResponse(StatusCodes.BadRequest, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val partners = new PartnersImpl(host, httpClient, credential, maxConnections)

    val result = partners.post(request).runWith(Sink.head)

    whenReady(result) { ex =>
      assert(ex.isLeft)
      assert(ex.left.get.isInstanceOf[MfcloudException])
    }
  }

  "#update" should "returns updated partner response" in {
    val request      = UpdatePartnerRequestFixtures.build
    val response     = PartnerResponseFixtures.build
    val httpEntity   = HttpEntity(`application/json`, response.asJson.noSpaces)
    val httpResponse = HttpResponse(StatusCodes.OK, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val partners = new PartnersImpl(host, httpClient, credential, maxConnections)

    partners
      .update(response.id, request)
      .runWith(TestSink.probe[Either[Throwable, PartnerResponse]])
      .requestNext(Right(response))
      .expectComplete()
  }

  it should "returns ParsingFailure exception when entity unmasshalled" in {
    val request      = UpdatePartnerRequestFixtures.build
    val httpEntity   = HttpEntity(`application/json`, "{")
    val httpResponse = HttpResponse(StatusCodes.OK, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val partners = new PartnersImpl(host, httpClient, credential, maxConnections)

    val result = partners.update("ID", request).runWith(Sink.head)

    whenReady(result) { ex =>
      assert(ex.isLeft)
      assert(ex.left.get.isInstanceOf[ParsingFailure])
    }
  }

  it should "returns MfcloudException when bad request" in {
    val request = UpdatePartnerRequestFixtures.build
    val httpEntity = HttpEntity(
      `application/json`,
      """
        |{
        |  "code" : "400",
        |  "errors" : [
        |    {
        |      "message" : "不正な都道府県名が渡されました。"
        |    }
        |  ]
        |}
      """.stripMargin
    )
    val httpResponse = HttpResponse(StatusCodes.BadRequest, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val partners = new PartnersImpl(host, httpClient, credential, maxConnections)

    val result = partners.update("ID", request).runWith(Sink.head)

    whenReady(result) { ex =>
      assert(ex.isLeft)
      assert(ex.left.get.isInstanceOf[MfcloudException])
    }
  }

  "#delete" should "returns empty response" in {
    val partnerId    = "ID"
    val httpResponse = HttpResponse(StatusCodes.NoContent)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val partners = new PartnersImpl(host, httpClient, credential, maxConnections)

    partners
      .delete(partnerId)
      .runWith(TestSink.probe[Either[Throwable, Done]])
      .requestNext(Right(Done))
      .expectComplete()
  }

  it should "returns MfcloudException when not found" in {
    val httpEntity = HttpEntity(
      `application/json`,
      """
        |{
        |  "code" : "404",
        |  "errors" : [
        |    {
        |      "message" : "存在しないIDが渡されました。"
        |    }
        |  ]
        |}
      """.stripMargin
    )
    val httpResponse = HttpResponse(StatusCodes.NotFound, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val partners = new PartnersImpl(host, httpClient, credential, maxConnections)

    val result = partners.delete("ID").runWith(Sink.head)

    whenReady(result) { ex =>
      assert(ex.isLeft)
      assert(ex.left.get.isInstanceOf[MfcloudException])
    }
  }
}
