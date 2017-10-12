package com.github.uchibori3.mfcloud.invoice.service

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{ HttpEntity, HttpRequest, HttpResponse, StatusCodes }
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Sink }
import akka.stream.testkit.scaladsl.TestSink
import com.github.uchibori3.mfcloud.invoice.Error.MfcloudException
import com.github.uchibori3.mfcloud.invoice.response.BillResponse
import com.github.uchibori3.mfcloud.invoice.HttpClient
import com.github.uchibori3.mfcloud.invoice.testkit.{ BillResponseFixtures, CreateBillRequestFixtures }
import io.circe.ParsingFailure
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ BeforeAndAfterAll, DiagrammedAssertions, FlatSpec }
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class BillsSpec extends FlatSpec with DiagrammedAssertions with BeforeAndAfterAll with MockFactory with ScalaFutures {
  implicit val system       = ActorSystem("bills")
  implicit val executor     = system.dispatcher
  implicit val materializer = ActorMaterializer()

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), Duration.Inf)
  }

  private val token          = "e60aa4e706e05aa73a3494f0b54f7b8d81e0542897708e2d64703c52cb40af02"
  private val credential     = OAuth2BearerToken(token)
  private val host           = "invoice.moneyforward.com"
  private val maxConnections = 4

  "#post" should "returns bill response" in {
    val request      = CreateBillRequestFixtures.build
    val response     = BillResponseFixtures.build
    val httpEntity   = HttpEntity(`application/json`, response.asJson.noSpaces)
    val httpResponse = HttpResponse(StatusCodes.Created, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val bills = new BillsImpl(host, httpClient, credential, maxConnections)

    bills
      .post(request)
      .runWith(TestSink.probe[Either[Throwable, BillResponse]])
      .requestNext(Right(response))
      .expectComplete()
  }

  it should "returns failure when entity unmarshalled" in {
    val request      = CreateBillRequestFixtures.build
    val httpEntity   = HttpEntity(`application/json`, "{")
    val httpResponse = HttpResponse(StatusCodes.Created, Nil, httpEntity)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val bills = new BillsImpl(host, httpClient, credential, maxConnections)

    val result = bills.post(request).runWith(Sink.head)

    whenReady(result) { ex =>
      assert(ex.isLeft)
      assert(ex.left.get.isInstanceOf[ParsingFailure])
    }
  }

  it should "returns MfcloudException when department id is not found" in {
    val request = CreateBillRequestFixtures.build
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

    val bills = new BillsImpl(host, httpClient, credential, maxConnections)

    val result = bills.post(request).runWith(Sink.head)

    whenReady(result) { ex =>
      assert(ex.isLeft)
      assert(ex.left.get.isInstanceOf[MfcloudException])
    }
  }

  "#getPdf" should "returns http response" in {
    val billId       = "ID"
    val httpResponse = HttpResponse()
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val bills = new BillsImpl(host, httpClient, credential, maxConnections)

    bills
      .getPdf(billId)
      .runWith(TestSink.probe[Either[Throwable, HttpResponse]])
      .requestNext(Right(httpResponse))
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

    val bills = new BillsImpl(host, httpClient, credential, maxConnections)

    val result = bills.getPdf("ID").runWith(Sink.head)

    whenReady(result) { ex =>
      assert(ex.isLeft)
      assert(ex.left.get.isInstanceOf[MfcloudException])
    }
  }

  "#delete" should "returns empty response" in {
    val billId       = "ID"
    val httpResponse = HttpResponse(StatusCodes.NoContent)
    val httpClient   = mock[HttpClient]
    (httpClient.connectionHttps _).expects(*, *, *).returning(Flow[HttpRequest].map(_ => httpResponse))

    val bills = new BillsImpl(host, httpClient, credential, maxConnections)

    bills
      .delete(billId)
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

    val bills = new BillsImpl(host, httpClient, credential, maxConnections)

    val result = bills.delete("ID").runWith(Sink.head)

    whenReady(result) { ex =>
      assert(ex.isLeft)
      assert(ex.left.get.isInstanceOf[MfcloudException])
    }
  }
}
