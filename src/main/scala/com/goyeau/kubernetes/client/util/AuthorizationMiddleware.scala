package com.goyeau.kubernetes.client.util

import cats.effect.Bracket
import org.http4s.Request
import org.http4s.client.Client
import org.http4s.headers.Authorization

object AuthorizationMiddleware {

  def apply[F[_]](client: Client[F], authorizations: Option[Authorization])(
    implicit F: Bracket[F, Throwable]
  ): Client[F] =
    Client[F] { req =>
      val withHeader: Request[F] = req.headers
        .get(Authorization)
        .fold(req.putHeaders(authorizations.toSeq: _*))(_ => req)
      client.run(withHeader)
    }

}
