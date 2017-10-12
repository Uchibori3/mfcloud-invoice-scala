package com.github.uchibori3.mfcloud.invoice

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import com.github.uchibori3.mfcloud.invoice.service.{ Bills, BillsImpl, Partners, PartnersImpl }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.AutoDerivation

trait MfcloudApiClient {
  val partners: Partners
  val bills: Bills
}

class MfcloudApiClientImpl(
    token: String,
    client: HttpClient
)(implicit system: ActorSystem)
    extends MfcloudApiClient
    with FailFastCirceSupport
    with AutoDerivation {

  private val credential     = OAuth2BearerToken(token)
  private val host           = "invoice.moneyforward.com"
  private val maxConnections = 4

  override val partners: Partners = new PartnersImpl(host, client, credential, maxConnections)
  override val bills: Bills       = new BillsImpl(host, client, credential, maxConnections)
}
