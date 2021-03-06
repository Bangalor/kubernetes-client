package com.goyeau.kubernetes.client.api

import cats.effect.Sync
import com.goyeau.kubernetes.client.operation._
import io.circe._
import io.k8s.api.core.v1.{ConfigMap, ConfigMapList}
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.implicits._

private[client] case class ConfigMapsApi[F[_]](httpClient: Client[F], server: Uri)(
  implicit
  val F: Sync[F],
  val listDecoder: Decoder[ConfigMapList],
  encoder: Encoder[ConfigMap],
  decoder: Decoder[ConfigMap]
) extends Listable[F, ConfigMapList] {
  val resourceUri = uri"/api" / "v1" / "configmaps"

  def namespace(namespace: String) = NamespacedConfigMapsApi(httpClient, server, namespace)
}

private[client] case class NamespacedConfigMapsApi[F[_]](
  httpClient: Client[F],
  server: Uri,
  namespace: String
)(
  implicit
  val F: Sync[F],
  val resourceEncoder: Encoder[ConfigMap],
  val resourceDecoder: Decoder[ConfigMap],
  val listDecoder: Decoder[ConfigMapList]
) extends Creatable[F, ConfigMap]
    with Replaceable[F, ConfigMap]
    with Gettable[F, ConfigMap]
    with Listable[F, ConfigMapList]
    with Deletable[F]
    with GroupDeletable[F] {
  val resourceUri = uri"/api" / "v1" / "namespaces" / namespace / "configmaps"
}
