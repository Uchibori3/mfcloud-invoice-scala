package com.github.uchibori3.mfcloud.invoice.response

case class BillResponse(
    id: String,
    pdf_url: String,
    operator_id: String,
    partner_id: String,
    department_id: String,
    member_id: String,
    member_name: Option[String],
    partner_name: String,
    partner_name_suffix: String,
    partner_detail: String,
    office_name: String,
    office_detail: String,
    title: Option[String],
    memo: Option[String],
    payment_condition: Option[String],
    excise_price: Double,
    subtotal: Double,
    total_price: Double,
    billing_date: String,
    due_date: String,
    sales_date: String,
    billing_number: Long,
    note: Option[String],
    document_name: Option[String],
    created_at: String,
    updated_at: String,
    tags: Seq[String],
    items: Seq[Item],
    status: Status
)

case class Item(
    id: String,
    name: Option[String],
    code: Option[String],
    detail: Option[String],
    unit_price: Option[Int],
    unit: Option[String],
    quantity: Option[Int],
    price: Option[Int],
    display_order: Int,
    excise: Boolean,
    created_at: String,
    updated_at: String
)

case class Status(
    email: String,
    payment: String,
    posting: String,
    download: String
)
