package com.benkio.replieseditor.module

import io.circe.parser.decode
import munit.FunSuite

class ModelsSuite extends FunSuite {

  test("ApiBot decodes from json") {
    val json = """{ "botId": "cala", "botName": "CalandroBot" }"""
    val res  = decode[ApiBot](json)
    assertEquals(res.toOption, Some(ApiBot("cala", "CalandroBot")))
  }

  test("RepliesChunk decodes from json") {
    val json =
      """{
        |  "total": 2,
        |  "offset": 0,
        |  "items": [
        |    { "index": 10, "value": { "a": 1 } },
        |    { "index": 11, "value": { "b": 2 } }
        |  ]
        |}""".stripMargin

    val res = decode[RepliesChunk](json)
    assert(res.isRight)
    assertEquals(res.toOption.map(_.total), Some(2))
    assertEquals(res.toOption.map(_.items.map(_.index)), Some(Vector(10, 11)))
  }
}
