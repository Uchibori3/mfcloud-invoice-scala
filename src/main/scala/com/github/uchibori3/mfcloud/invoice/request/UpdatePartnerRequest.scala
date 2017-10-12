package com.github.uchibori3.mfcloud.invoice.request

case class UpdatePartnerRequest(
  partner: UpdatePartner
)

case class UpdatePartner(
  code: Option[String],
  name: String,
  name_kana: Option[String],
  name_suffix: Option[String],
  memo: Option[String],
  departments: Option[Seq[UpdateDepartment]]
)

case class UpdateDepartment(
  id: String,
  name: Option[String],
  zip: Option[String],
  tel: Option[String],
  prefecture: String,
  address1: Option[String],
  address2: Option[String],
  person_title: Option[String],
  person_name: Option[String],
  email: Option[String],
  cc_emails: Option[String],
)

