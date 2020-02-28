package com.goyeau.kubernetes.client

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import cats.effect._
import com.goyeau.kubernetes.client.api._
import com.goyeau.kubernetes.client.util.{AuthorizationMiddleware, SslContexts}

import scala.concurrent.ExecutionContext

case class KubernetesClient[F[_]: ConcurrentEffect](httpClient: Client[F], config: KubeConfig) {
  lazy val namespaces = NamespacesApi(httpClient, config.server)
  lazy val pods = PodsApi(httpClient, config.server, config.authorization, SslContexts.fromConfig(config))
  lazy val jobs = JobsApi(httpClient, config.server)
  lazy val cronJobs = CronJobsApi(httpClient, config.server)
  lazy val deployments = DeploymentsApi(httpClient, config.server)
  lazy val statefulSets = StatefulSetsApi(httpClient, config.server)
  lazy val replicaSets = ReplicaSetsApi(httpClient, config.server)
  lazy val services = ServicesApi(httpClient, config.server)
  lazy val serviceAccounts = ServiceAccountsApi(httpClient, config.server)
  lazy val configMaps = ConfigMapsApi(httpClient, config.server)
  lazy val secrets = SecretsApi(httpClient, config.server)
  lazy val horizontalPodAutoscalers = HorizontalPodAutoscalersApi(httpClient, config.server)
  lazy val podDisruptionBudgets = PodDisruptionBudgetsApi(httpClient, config.server)
}

object KubernetesClient {
  def apply[F[_]: ConcurrentEffect](config: KubeConfig): Resource[F, KubernetesClient[F]] =
    BlazeClientBuilder[F](ExecutionContext.global, Option(SslContexts.fromConfig(config))).resource
      .map(httpClient => apply(AuthorizationMiddleware(httpClient, config.authorization), config))

  def apply[F[_]: ConcurrentEffect](config: F[KubeConfig]): Resource[F, KubernetesClient[F]] =
    Resource.liftF(config).flatMap(apply(_))
}
