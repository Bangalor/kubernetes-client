package com.goyeau.kubernetes.client.operation

import cats.effect.Sync
import com.goyeau.kubernetes.client.util.EnrichedStatus
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.Method._

trait GroupDeletable[F[_]] extends Http4sClientDsl[F] {
  protected def httpClient: Client[F]
  implicit protected val F: Sync[F]
  protected def server: Uri
  protected def resourceUri: Uri

  lazy val delete: F[Status] =
    httpClient.fetch(DELETE(server.resolve(resourceUri)))(EnrichedStatus[F])
}
