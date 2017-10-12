package com.github.uchibori3.mfcloud.invoice.testkit

import com.github.uchibori3.mfcloud.invoice.request.{ CreatePartner, CreatePartnerRequest }

object CreatePartnerRequestFixtures {
  def build: CreatePartnerRequest = CreatePartnerRequest(
    partner = CreatePartner(
      code = None,
      name = "test partner",
      name_kana = Some("テストパートナー"),
      name_suffix = Some("様"),
      memo = Some("memo"),
      zip = Some("1234567"),
      tel = Some("9876543"),
      prefecture = Some("東京都"),
      address1 = Some("address 1"),
      address2 = Some("address 2-3-4"),
      person_name = Some("test person"),
      person_title = Some("test person title"),
      department_name = Some("test department"),
      email = Some("test@example.com"),
      cc_emails = Some("test1@exapmle.com, test2@example.com")
    )
  )
}
