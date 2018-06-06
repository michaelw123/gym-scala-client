package gym.scala.client

import akka.http.scaladsl.model.ContentTypes
import gym.scala.client.GymSpace.GymAllEnvs
import akka.actor.{ActorSystem, Props}

/**
  * Created by wangmich on 06/06/2018.
  */
class GymEnv {
  var _host:String="http://127.0.0.1"
  var _port:Int=5000
  var _timeout=10
  val _contentType = ContentTypes.`application/json`
  def host(h:String): this.type = {
    _host=h
    this
  }
  def port(p:Int):this.type = {
    _port = p
    this
  }
  def timeout(t:Int):this.type = {
    _timeout = t
    this
  }
}
object GymEnv {
  def listEnv: GymAllEnvs = {
     val listEnvs = new listEnvs
     gymAgent.execute(listEnvs)
  }
}
