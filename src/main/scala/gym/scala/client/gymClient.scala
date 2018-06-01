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

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.unmarshalling._
import akka.stream.ActorMaterializer
import gym.scala.client.GymSpace._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}


/**
  * Created by Michael Wang on 04/26/2018.
  */
trait gymClient{
  def execute[T, S](command: T): S
}
object gymClient {
  implicit val system = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  var _host:String="http://127.0.0.1"
  var _port:Int=5000
  var _timeout=10
  val contentType = ContentTypes.`application/json`
  def terminate = system.terminate
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
  def reqResp(command:GymApi):HttpResponse = {
    val uri = _host + ":" + _port + command.uri
    val httpRequest = HttpRequest(uri = uri).withMethod(command.method).withEntity(HttpEntity(contentType, command.json))
    val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)
    Await.result(responseFuture, _timeout.second)
  }
   def execute(command:listEnvs): GymAllEnvs = {
    val resp = reqResp(command)
    println(s"resp=$resp")
    val envs:GymAllEnvs = resp match {
      case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[GymAllEnvs], _timeout.second)
    }
    envs
  }
   def execute(command:createEnv):GymInstance = {
    val resp = reqResp(command)
    val instance:GymInstance = resp match {
      case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[GymInstance], _timeout.second)
    }
    instance
  }
   def execute(command:resetEnv):Observation = {
    val resp = reqResp(command)
    val Observation:Observation = resp match {
      case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[Observation], _timeout.second)
    }
    Observation
  }
   def execute(command:actionSpace):ActionSpace = {
    val resp = reqResp(command)
    val actionSpace:ActionSpace = resp match {
      case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[ActionSpace], _timeout.second)
    }
    actionSpace
  }

  implicit def execute(command:obsSpace):ObservationSpace = {
    val resp = reqResp(command)
    val observationSpace:ObservationSpace = resp match {
      case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[ObservationSpace], _timeout.second)
    }
    observationSpace
  }
  implicit def execute(command:monitorStart) = reqResp(command)

  implicit def execute(command:monitorClose) = reqResp(command)
  implicit def execute(command:shutdown) = reqResp(command)

  implicit def execute(command:step):StepReply = {
    val resp = reqResp(command)
    val stepReply:StepReply = resp match {
      case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[StepReply], _timeout.second)
    }
    stepReply
  }
}
