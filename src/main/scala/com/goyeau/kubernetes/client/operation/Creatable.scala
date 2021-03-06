package com.goyeau.kubernetes.client.operation

import scala.language.reflectiveCalls
import cats.implicits._
import cats.effect.Sync
import com.goyeau.kubernetes.client.util.CirceEntityCodec._
import com.goyeau.kubernetes.client.util.EnrichedStatus
import io.circe._
import io.k8s.apimachinery.pkg.apis.meta.v1.ObjectMeta
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.`Content-Type`
import org.http4s.Method._

trait Creatable[F[_], Resource <: { def metadata: Option[ObjectMeta] }] extends Http4sClientDsl[F] {
  protected def httpClient: Client[F]
  implicit protected val F: Sync[F]
  protected def server: Uri
  protected def resourceUri: Uri
  implicit protected def resourceEncoder: Encoder[Resource]

  def create(resource: Resource): F[Status] =
    httpClient.fetch(POST(resource, server.resolve(resourceUri)))(
      EnrichedStatus[F]
    )

  def createOrUpdate(resource: Resource): F[Status] = {
    val fullResourceUri = server.resolve(resourceUri) / resource.metadata.get.name.get
    def update =
      httpClient.fetch(
        PATCH(
          resource,
          fullResourceUri,
          `Content-Type`(MediaType.application.`merge-patch+json`)
        )
      )(EnrichedStatus[F])

    httpClient
      .fetch(GET(fullResourceUri))(EnrichedStatus.apply[F])
      .flatMap {
        case status if status.isSuccess => update
        case Status.NotFound =>
          create(resource).recoverWith {
            case Status.Conflict => update
          }
      }
  }
}
