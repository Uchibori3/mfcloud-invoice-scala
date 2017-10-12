package com.github.uchibori3.mfcloud.invoice

import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }

import scala.concurrent.duration._

trait HttpClient {
  def connection(scheme: String, host: String, port: Int, timeout: Int): Flow[HttpRequest, HttpResponse, _]
  def connectionHttp(host: String, port: Int = 80, timeout: Int = 15): Flow[HttpRequest, HttpResponse, _]
  def connectionHttps(host: String, port: Int = 443, timeout: Int = 15): Flow[HttpRequest, HttpResponse, _]
}

class HttpClientImpl(implicit actorSystem: ActorSystem) extends HttpClient {
  override def connection(scheme: String, host: String, port: Int, timeout: Int): Flow[HttpRequest, HttpResponse, _] =
    scheme match {
      case "http"  => connectionHttp(host, port, timeout)
      case "https" => connectionHttps(host, port, timeout)
      case _       => connectionHttp(host, port, timeout)
    }

  override def connectionHttp(host: String, port: Int, timeout: Int): Flow[HttpRequest, HttpResponse, _] =
    Http().outgoingConnection(host, port).idleTimeout(timeout.seconds)

  override def connectionHttps(host: String, port: Int, timeout: Int): Flow[HttpRequest, HttpResponse, _] =
    Http().outgoingConnectionHttps(host, port).idleTimeout(timeout.seconds)
}
