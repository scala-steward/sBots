package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.implicits.*

class StaticAssetsEndpointSpec extends CatsEffectSuite {

  test("GET / serves index.html from resources") {
    val app = StaticAssetsEndpoint.routes.orNotFound
    val req = Request[IO](Method.GET, uri"/")
    app.run(req).flatMap { resp =>
      IO.blocking(resp.status).map { st =>
        assertEquals(st, Status.Ok)
      }
    }
  }

  test("GET /public/missing returns 404") {
    val app = StaticAssetsEndpoint.routes.orNotFound
    val req = Request[IO](Method.GET, uri"/public/definitely-not-here.js")
    app.run(req).map { resp =>
      assertEquals(resp.status, Status.NotFound)
    }
  }
}

