package com.github.uchibori3.mfcloud.invoice.testkit

import akka.http.scaladsl.model.DateTime
import com.github.uchibori3.mfcloud.invoice.response.{ Department, PartnerResponse }

import scala.util.Random

object PartnerResponseFixtures {
  def build: PartnerResponse = PartnerResponse(
    id = Random.alphanumeric.take(22).mkString,
    code = Some(Random.alphanumeric.take(10).mkString),
    name = "test partner",
    name_kana = Some("テストパートナー"),
    name_suffix = "様",
    memo = Some("memo"),
    created_at = DateTime.now.toString(),
    updated_at = DateTime.now.toString(),
    departments = Seq(
      Department(
        id = Random.alphanumeric.take(22).mkString,
        name = Some("test department"),
        zip = Some("1234567"),
        tel = Some("9876543"),
        prefecture = "東京都",
        address1 = Some("address 1"),
        address2 = Some("address 2-3-4"),
        person_name = Some("test person"),
        person_title = Some("test person title"),
        email = Some("test@example.com"),
        cc_emails = Some("test1@exapmle.com, test2@example.com"),
        created_at = DateTime.now.toString(),
        updated_at = DateTime.now.toString()
      )
    )
  )
}
