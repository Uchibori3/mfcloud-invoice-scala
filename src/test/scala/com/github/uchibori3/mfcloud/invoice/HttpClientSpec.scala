package com.github.uchibori3.mfcloud.invoice

import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import org.scalatest.{ BeforeAndAfterAll, DiagrammedAssertions, FlatSpec }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class HttpClientSpec extends FlatSpec with DiagrammedAssertions with BeforeAndAfterAll {
  implicit val system   = ActorSystem("http-client-system")
  implicit val executor = system.dispatcher

  override protected def afterAll(): Unit = {
    Await.ready(system.terminate(), Duration.Inf)
  }

  "#connection" should "returns outgoing connection flow with http scheme" in {
    val httpClient = new HttpClientImpl()
    val connection = httpClient.connection("http", "127.0.0.1", 8002, 5)

    assert(connection.isInstanceOf[Flow[_, _, _]])
  }

  it should "returns outgoing connection flow with https scheme" in {
    val httpClient = new HttpClientImpl()
    val connection = httpClient.connection("https", "127.0.0.1", 8002, 5)

    assert(connection.isInstanceOf[Flow[_, _, _]])
  }

  it should "returns outgoing connection flow with unknown scheme" in {
    val httpClient = new HttpClientImpl()
    val connection = httpClient.connection("tcp", "127.0.0.1", 8002, 5)

    assert(connection.isInstanceOf[Flow[_, _, _]])
  }

  "#connectionHttp" should "returns outgoing connection flow" in {
    val httpClient = new HttpClientImpl()
    val connection = httpClient.connectionHttp("127.0.0.1", 8002, 5)

    assert(connection.isInstanceOf[Flow[_, _, _]])
  }

  "#connectionHttps" should "returns outgoing connection flow" in {
    val httpClient = new HttpClientImpl()
    val connection = httpClient.connectionHttps("127.0.0.1", 8002, 5)

    assert(connection.isInstanceOf[Flow[_, _, _]])
  }
}
