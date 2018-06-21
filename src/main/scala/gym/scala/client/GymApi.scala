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
package gym.scala.client

import akka.http.scaladsl.model.{ContentTypes, HttpMethods}
import gym.scala.client.GymSpace._

trait GymApi {
  protected val envRoot = "/v1/envs/"
  protected val gymInstance:GymInstance = GymInstance.apply("")
  val json:String = """{}"""
  val uri=envRoot
  val method = HttpMethods.POST
}
final case class createEnv( val envId:String) extends GymApi {
  override val json = s"""{ "env_id": "${envId}" }"""
}
final case class listEnvs(override val gymInstance:GymInstance = GymInstance.apply("")) extends GymApi{
  override val method = HttpMethods.GET
}
final case class resetEnv(override val gymInstance:GymInstance) extends GymApi{
  override val uri=s"${envRoot}${gymInstance.instance_id}/reset/"
}
final case class step(override val gymInstance:GymInstance, action:Int, render:Boolean=false) extends GymApi {
  override val json = s"""{ "action": ${action}, "render": ${render} }"""
  override val uri=s"${envRoot}${gymInstance.instance_id}/step/"
}

final case class actionSpace(override val gymInstance:GymInstance) extends GymApi{
  override val method = HttpMethods.GET
  override val uri=s"${envRoot}${gymInstance.instance_id}/action_space/"
}
final case class obsSpace(override val gymInstance:GymInstance) extends GymApi{
  override val method = HttpMethods.GET
  override val uri=s"${envRoot}${gymInstance.instance_id}/observation_space/"
}
final case class monitorStart(override val gymInstance:GymInstance) extends GymApi{
  override val json = s"""{ "resume": false, "directory": "/openai/tmp", "force": true }"""
  override val uri=s"${envRoot}${gymInstance.instance_id}/monitor/start/"
}
final case class monitorClose(override val gymInstance:GymInstance) extends GymApi{
  override val uri=s"${envRoot}${gymInstance.instance_id}/monitor/close/"
}
//case class monitorUpload(instanceId:Option[String]) extends GymApi
final case class shutdown() extends GymApi {
  override val uri="/v1/shutdown/"
}


