package gym.scala.client

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}

/**
  * Created by wangmich on 05/01/2018.
  */
sealed trait GymApi {
  val contentType = ContentTypes.`application/json`
  val envRoot = "/v1/envs/"
  val method = HttpMethods.POST
  val instanceId:Option[String]
}
case class createEnv(instanceId:Option[String] = None, envId:String) extends GymApi {
  override val method = HttpMethods.POST
  val source = """{ "env_id": "CartPole-v0" }"""
}
case class listEnvs(instanceId:Option[String]) extends GymApi{
  override val method = HttpMethods.GET
  val source = """{ "env_id": "CartPole-v0" }"""
}
case class resetEnv(instanceId:Option[String]) extends GymApi{
  override val method = HttpMethods.POST
  val source = """{ "instance_id": "${instanceId.get}" }"""
}
case class step(instanceId:Option[String]) extends GymApi
case class actionSpace(instanceId:Option[String]) extends GymApi
case class obsSpace(instanceId:Option[String]) extends GymApi
case class monitorStart(instanceId:Option[String]) extends GymApi
case class monitorFlush(instanceId:Option[String]) extends GymApi
case class monitorUpload(instanceId:Option[String]) extends GymApi
case class shutdown(instanceId:Option[String]=None) extends GymApi


