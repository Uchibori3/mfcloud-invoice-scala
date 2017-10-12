package com.github.uchibori3.mfcloud.invoice.testkit

import com.github.uchibori3.mfcloud.invoice.request.{ CreateBill, CreateBillRequest, CreateItem }

object CreateBillRequestFixtures {
  def build: CreateBillRequest = CreateBillRequest(
    billing = CreateBill(
      department_id = "DzsQn9e8Wb48Dpz1iwmjew",
      title = None,
      billing_number = None,
      payment_condition = None,
      note = None,
      billing_date = None,
      due_date = None,
      sales_date = None,
      memo = None,
      document_name = None,
      tags = None,
      items = Seq(
        CreateItem(
          name = Some("item1"),
          code = None,
          detail = None,
          quantity = Some(10),
          unit_price = Some(100),
          unit = Some("unit"),
          excise = None
        )
      )
    )
  )
}
