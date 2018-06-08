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

object gymClient {
  implicit val system = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  var _host: String = "http://127.0.0.1"
  var _port: Int = 5000
  var _timeout = 10
  val contentType = ContentTypes.`application/json`

  def terminate = system.terminate

  def host(h: String): this.type = {
    _host = h
    this
  }

  def port(p: Int): this.type = {
    _port = p
    this
  }

  def timeout(t: Int): this.type = {
    _timeout = t
    this
  }

  def execute[A, R](command: A)(implicit ev: Execution[A, R]): R = ev.execute(command)

  def reqResp(command: GymApi): HttpResponse = {
    val uri = _host + ":" + _port + command.uri
    val httpRequest = HttpRequest(uri = uri).withMethod(command.method).withEntity(HttpEntity(contentType, command.json))
    val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)
    Await.result(responseFuture, _timeout.second)
  }

/*
A type class that executes gymApi

 */
  trait Execution[A, R] {
    def execute(command: A): R
  }

  object Execution {

    implicit object ListEnvExecution extends Execution[listEnvs, GymAllEnvs] {
      def execute(command: listEnvs): GymAllEnvs = {
        val resp = gymClient.reqResp(command)
        val envs: GymAllEnvs = resp match {
          case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[GymAllEnvs], gymClient._timeout.second)
        }
        envs
      }
    }

    implicit object CreateEnvExecution extends Execution[createEnv, GymInstance] {
      def execute(command: createEnv): GymInstance = {
        val resp = gymClient.reqResp(command)
        val envs: GymInstance = resp match {
          case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[GymInstance], gymClient._timeout.second)
        }
        envs
      }
    }

    implicit object ResetEnvExecution extends Execution[resetEnv, Observation] {
      def execute(command: resetEnv): Observation = {
        val resp = gymClient.reqResp(command)
        val envs: Observation = resp match {
          case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[Observation], gymClient._timeout.second)
        }
        envs
      }
    }

    implicit object ActionSpaceExecution extends Execution[actionSpace, ActionSpace] {
      def execute(command: actionSpace): ActionSpace = {
        val resp = gymClient.reqResp(command)
        val envs: ActionSpace = resp match {
          case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[ActionSpace], gymClient._timeout.second)
        }
        envs
      }
    }

    implicit object ObsSpaceExecution extends Execution[obsSpace, ObservationSpace] {
      def execute(command: obsSpace): ObservationSpace = {
        val resp = gymClient.reqResp(command)
        val envs: ObservationSpace = resp match {
          case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[ObservationSpace], gymClient._timeout.second)
        }
        println(envs)
        envs
      }
    }

    implicit object MonitorStartExecution extends Execution[monitorStart, Unit] {
      def execute(command: monitorStart): Unit = {
        gymClient.reqResp(command)
      }
    }

    implicit object MonitorCloseExecution extends Execution[monitorClose, Unit] {
      def execute(command: monitorClose): Unit = {
        gymClient.reqResp(command)
      }
    }

    implicit object ShutdownExecution extends Execution[shutdown, Unit] {
      def execute(command: shutdown): Unit = {
        gymClient.reqResp(command)
      }
    }

    implicit object StepExecution extends Execution[step, StepReply] {
      def execute(command: step): StepReply = {
        val resp = gymClient.reqResp(command)
        val envs: StepReply = resp match {
          case HttpResponse(StatusCodes.OK, headers, entity, _) => Await.result(Unmarshal(entity).to[StepReply], gymClient._timeout.second)
        }
        envs
      }
    }

  }

}
