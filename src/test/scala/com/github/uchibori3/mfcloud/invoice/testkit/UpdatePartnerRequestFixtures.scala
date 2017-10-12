package com.github.uchibori3.mfcloud.invoice.testkit

import com.github.uchibori3.mfcloud.invoice.request.{ UpdateDepartment, UpdatePartner, UpdatePartnerRequest }

object UpdatePartnerRequestFixtures {
  def build: UpdatePartnerRequest = UpdatePartnerRequest(
    partner = UpdatePartner(
      code = None,
      name = "modified partner",
      name_kana = Some("パートナー"),
      name_suffix = Some("殿"),
      memo = Some("modified memo"),
      departments = Some(
        Seq(
          UpdateDepartment(
            id = "ID",
            name = Some("modified department"),
            zip = Some("9876543"),
            tel = Some("1234567"),
            prefecture = "東京都",
            address1 = Some("modified address 1"),
            address2 = Some("modified address 2-3-4"),
            person_title = Some("modified person title"),
            person_name = Some("modified department"),
            email = Some("modified@example.com"),
            cc_emails = Some("modified1@exapmle.com, modified2@example.com")
          )
        )
      )
    )
  )
}
