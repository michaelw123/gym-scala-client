package gym.scala.client

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import com.typesafe.config.ConfigException.Null

/*
 * Copyright (c) 2017 Michael Wang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
sealed trait GymApi {
  val contentType = ContentTypes.`application/json`
  val envRoot = "/v1/envs/"
  val method = HttpMethods.POST
  val gymInstance:GymInstance = GymInstance.apply("")
  val source:String = """{}"""
  val uri=envRoot
}
case class createEnv( val envId:String) extends GymApi {
  override val source = s"""{ "env_id": "${envId}" }"""
}
case class listEnvs(override val gymInstance:GymInstance = GymInstance.apply("")) extends GymApi{
  override val method = HttpMethods.GET
}
case class resetEnv(override val gymInstance:GymInstance) extends GymApi{
  override val uri=s"${envRoot}${gymInstance.instance_id}/reset/"
}
case class step(override val gymInstance:GymInstance) extends GymApi {
  override val source = s"""{ "action": 0, "render": true }"""
  override val uri=s"${envRoot}${gymInstance.instance_id}/step/"
}

case class actionSpace(override val gymInstance:GymInstance) extends GymApi{
  override val method = HttpMethods.GET
  override val uri=s"${envRoot}${gymInstance.instance_id}/action_space/"
}
case class obsSpace(override val gymInstance:GymInstance) extends GymApi{
  override val method = HttpMethods.GET
  override val uri=s"${envRoot}${gymInstance.instance_id}/observation_space/"
}
case class monitorStart(override val gymInstance:GymInstance) extends GymApi{
  override val source = s"""{ "resume": false, "directory": "/openai/tmp", "force": false }"""
  override val uri=s"${envRoot}${gymInstance.instance_id}/monitor/start/"
}
case class monitorClose(override val gymInstance:GymInstance) extends GymApi{
  override val uri=s"${envRoot}${gymInstance.instance_id}/monitor/close/"
}
//case class monitorUpload(instanceId:Option[String]) extends GymApi
case class shutdown() extends GymApi {
  override val uri="/v1/shutdown/"
}


