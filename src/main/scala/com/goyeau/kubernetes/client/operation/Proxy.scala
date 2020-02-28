package com.goyeau.kubernetes.client.operation

import cats.effect.Sync
import org.http4s._
import org.http4s.dsl.impl.Path
import org.http4s.client.Client
import org.http4s.EntityDecoder
import org.http4s.headers.`Content-Type`

trait Proxy[F[_]] {
  protected def httpClient: Client[F]
  implicit protected val F: Sync[F]
  protected def server: Uri
  protected def resourceUri: Uri

  def proxy(
    name: String,
    method: Method,
    path: Path,
    contentType: `Content-Type` = `Content-Type`(MediaType.text.plain),
    data: Option[String] = None
  ): F[String] =
    httpClient.expect[String](
      Request(
        method,
        server.resolve(resourceUri) / name / s"proxy$path",
        body = data.fold[EntityBody[F]](EmptyBody)(
          implicitly[EntityEncoder[F, String]].withContentType(contentType).toEntity(_).body
        )
      )
    )(EntityDecoder.text)
}
