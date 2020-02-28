package com.goyeau.kubernetes.client.operation

import cats.effect.Sync
import com.goyeau.kubernetes.client.util.CirceEntityCodec._
import io.circe._
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.Method._

trait Listable[F[_], Resource] extends Http4sClientDsl[F] {
  protected def httpClient: Client[F]
  implicit protected val F: Sync[F]
  protected def server: Uri
  protected def resourceUri: Uri
  implicit protected def listDecoder: Decoder[Resource]

  lazy val list: F[Resource] =
    httpClient.expect[Resource](GET(server.resolve(resourceUri)))
}
