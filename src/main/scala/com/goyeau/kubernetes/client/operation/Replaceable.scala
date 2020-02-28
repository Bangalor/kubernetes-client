package com.goyeau.kubernetes.client.operation

import scala.language.reflectiveCalls
import cats.effect.Sync
import com.goyeau.kubernetes.client.util.CirceEntityCodec._
import com.goyeau.kubernetes.client.util.EnrichedStatus
import io.circe._
import io.k8s.apimachinery.pkg.apis.meta.v1.ObjectMeta
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.Method._

trait Replaceable[F[_], Resource <: { def metadata: Option[ObjectMeta] }] extends Http4sClientDsl[F] {
  protected def httpClient: Client[F]
  implicit protected val F: Sync[F]
  protected def server: Uri
  protected def resourceUri: Uri
  implicit protected def resourceEncoder: Encoder[Resource]

  def replace(resource: Resource): F[Status] =
    httpClient.fetch(
      PUT(
        resource,
        server.resolve(resourceUri) / resource.metadata.get.name.get
      )
    )(EnrichedStatus[F])
}
