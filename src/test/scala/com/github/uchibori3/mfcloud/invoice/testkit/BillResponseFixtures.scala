package com.github.uchibori3.mfcloud.invoice.testkit

import akka.http.scaladsl.model.DateTime
import com.github.uchibori3.mfcloud.invoice.response.{ BillResponse, Item, Status }

import scala.util.Random

object BillResponseFixtures {
  def build: BillResponse = {
    val id = Random.alphanumeric.take(22).mkString
    BillResponse(
      id = id,
      pdf_url = s"https://invoice.moneyforward.com/api/v1/billings/$id.pdf",
      operator_id = Random.alphanumeric.take(22).mkString,
      partner_id = Random.alphanumeric.take(22).mkString,
      department_id = Random.alphanumeric.take(22).mkString,
      member_id = Random.alphanumeric.take(22).mkString,
      member_name = Some("Member name"),
      partner_name = "Partner name",
      partner_name_suffix = "様",
      partner_detail = "partner detail",
      office_name = "Office name",
      office_detail = "Office detail",
      title = Some("Title"),
      memo = Some("Memo"),
      payment_condition = Some("Payment condition"),
      excise_price = 800,
      subtotal = 10000,
      total_price = 10800,
      billing_date = DateTime.now.toString(),
      due_date = DateTime.now.toString(),
      sales_date = DateTime.now.toString(),
      billing_number = Random.nextInt(100).toLong,
      note = Some("Note"),
      document_name = Some("Document name"),
      created_at = DateTime.now.toString(),
      updated_at = DateTime.now.toString(),
      tags = Seq("tag1", "tag2"),
      items = Seq[Item](
        Item(
          id = Random.alphanumeric.take(22).mkString,
          name = Some("Test item1"),
          code = None,
          detail = Some("Item 1 detail"),
          unit_price = Some(100),
          unit = Some("units"),
          quantity = Some(10),
          price = Some(1000),
          display_order = 0,
          excise = true,
          created_at = DateTime.now.toString(),
          updated_at = DateTime.now.toString()
        ),
        Item(
          id = Random.alphanumeric.take(22).mkString,
          name = Some("Test item2"),
          code = None,
          detail = Some("Item 2 detail"),
          unit_price = Some(1000),
          unit = Some("units"),
          quantity = Some(9),
          price = Some(9000),
          display_order = 1,
          excise = true,
          created_at = DateTime.now.toString(),
          updated_at = DateTime.now.toString()
        )
      ),
      status = Status(
        email = "Incomplete",
        payment = "Incomplete",
        posting = "Incomplete",
        download = "Complete"
      )
    )
  }
}
