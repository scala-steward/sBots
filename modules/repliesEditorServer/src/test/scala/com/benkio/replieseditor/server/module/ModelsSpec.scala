package com.benkio.replieseditor.server.module

import io.circe.Json
import io.circe.syntax.*
import io.circe.parser.decode
import munit.FunSuite

class ModelsSpec extends FunSuite {

  test("ApiError encodes to json") {
    val j = ApiError("boom", details = Some(Json.obj("x" -> Json.fromInt(1)))).asJson
    assertEquals(j.hcursor.downField("error").as[String].toOption, Some("boom"))
    assertEquals(j.hcursor.downField("details").downField("x").as[Int].toOption, Some(1))
  }

  test("FilterReq decodes from json") {
    val res = decode[FilterReq]("""{ "message": "hi" }""")
    assertEquals(res.toOption, Some(FilterReq("hi")))
  }
}

