package com.github.uchibori3.mfcloud.invoice.request

case class CreatePartnerRequest(
    partner: CreatePartner
)

case class CreatePartner(
    code: Option[String],
    name: String,
    name_kana: Option[String],
    name_suffix: Option[String],
    memo: Option[String],
    zip: Option[String],
    tel: Option[String],
    prefecture: Option[String],
    address1: Option[String],
    address2: Option[String],
    person_name: Option[String],
    person_title: Option[String],
    department_name: Option[String],
    email: Option[String],
    cc_emails: Option[String]
)
