package com.github.uchibori3.mfcloud.invoice.response

case class PartnerResponse(
    id: String,
    code: Option[String],
    name: String,
    name_kana: Option[String],
    name_suffix: String,
    memo: Option[String],
    created_at: String,
    updated_at: String,
    departments: Seq[Department]
)

case class Department(
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
    created_at: String,
    updated_at: String
)
