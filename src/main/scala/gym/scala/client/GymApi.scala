package gym.scala.client

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}

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
  val instanceId:GymInstance = null
  val source:String
}
case class createEnv( val envId:String) extends GymApi {
  override val method = HttpMethods.POST
  override val source = s"""{ "env_id": "${envId}" }"""
}
case class listEnvs(override val instanceId:GymInstance = null) extends GymApi{
  override val method = HttpMethods.GET
  override val source = """{}"""
}
case class resetEnv(override val instanceId:GymInstance) extends GymApi{
  override val method = HttpMethods.POST
  override val source = """{ "instance_id": "${instanceId.instance_id_}" }"""
}
//case class step(instanceId:Option[String]) extends GymApi
//case class actionSpace(instanceId:Option[String]) extends GymApi
//case class obsSpace(instanceId:Option[String]) extends GymApi
//case class monitorStart(instanceId:Option[String]) extends GymApi
//case class monitorFlush(instanceId:Option[String]) extends GymApi
//case class monitorUpload(instanceId:Option[String]) extends GymApi
//case class shutdown(instanceId:Option[String]=None) extends GymApi


