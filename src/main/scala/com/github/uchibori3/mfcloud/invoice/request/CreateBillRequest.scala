package com.github.uchibori3.mfcloud.invoice.request

case class CreateBillRequest(
    billing: CreateBill
)

case class CreateBill(
    department_id: String,
    title: Option[String],
    billing_number: Option[String],
    payment_condition: Option[String],
    note: Option[String],
    billing_date: Option[String],
    due_date: Option[String],
    sales_date: Option[String],
    memo: Option[String],
    document_name: Option[String],
    tags: Option[String],
    items: Seq[CreateItem]
)

case class CreateItem(
    name: Option[String],
    code: Option[String],
    detail: Option[String],
    quantity: Option[Int],
    unit_price: Option[Int],
    unit: Option[String],
    excise: Option[Int]
)
