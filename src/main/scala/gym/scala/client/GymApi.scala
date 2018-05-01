package gym.scala.client

/**
  * Created by wangmich on 05/01/2018.
  */
trait GymApi {
  val instanceId:Option[String]
}
case class createEnv(instanceId:Option[String] = None, envId:String) extends GymApi
case class listEnvs(instanceId:Option[String]) extends GymApi
case class resetEnv(instanceId:Option[String]) extends GymApi
case class step(instanceId:Option[String]) extends GymApi
case class actionSpace(instanceId:Option[String]) extends GymApi
case class obsSpace(instanceId:Option[String]) extends GymApi
case class monitorStart(instanceId:Option[String]) extends GymApi
case class monitorFlush(instanceId:Option[String]) extends GymApi
case class monitorUpload(instanceId:Option[String]) extends GymApi
case class shutdown(instanceId:Option[String]=None) extends GymApi


