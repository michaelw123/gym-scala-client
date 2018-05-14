package gym.scala.client

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by MichaelXiaoqun on 2018-05-13.
  */
case class GymInstance(instance_id:String)
object GymInstance extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val gymInstanceFormat = jsonFormat1(GymInstance.apply)
}
//  case class GymEnv (gymInstance:GymInstance, env_id:String)
//  object GymEnv extends DefaultJsonProtocol with SprayJsonSupport {
//    implicit val gymEnvFormat = jsonFormat2(GymEnv.apply)
//  }

case class GymAllEnvs(all_envs:Map[String, String])
object GymAllEnvs extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val gymAllEnvsFormat = jsonFormat1(GymAllEnvs.apply)
}