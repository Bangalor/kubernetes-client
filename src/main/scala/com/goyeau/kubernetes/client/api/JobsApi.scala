package com.goyeau.kubernetes.client.api

import cats.effect.Sync
import com.goyeau.kubernetes.client.operation._
import io.circe._
import io.k8s.api.batch.v1.{Job, JobList}
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.implicits._

private[client] case class JobsApi[F[_]](httpClient: Client[F], server: Uri)(
  implicit
  val F: Sync[F],
  val listDecoder: Decoder[JobList],
  encoder: Encoder[Job],
  decoder: Decoder[Job]
) extends Listable[F, JobList] {
  val resourceUri = uri"/apis" / "batch" / "v1" / "jobs"

  def namespace(namespace: String) = NamespacedJobsApi(httpClient, server, namespace)
}

private[client] case class NamespacedJobsApi[F[_]](
  httpClient: Client[F],
  server: Uri,
  namespace: String
)(
  implicit
  val F: Sync[F],
  val resourceEncoder: Encoder[Job],
  val resourceDecoder: Decoder[Job],
  val listDecoder: Decoder[JobList]
) extends Creatable[F, Job]
    with Replaceable[F, Job]
    with Gettable[F, Job]
    with Listable[F, JobList]
    with Deletable[F]
    with DeletableTerminated[F]
    with GroupDeletable[F] {
  val resourceUri = uri"/apis" / "batch" / "v1" / "namespaces" / namespace / "jobs"
}
