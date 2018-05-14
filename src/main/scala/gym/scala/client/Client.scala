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

import spray.json._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import scala.concurrent.duration._
import akka.stream.ActorMaterializer
import akka.actor.Actor
import akka.http.scaladsl.unmarshalling._
import akka.http.scaladsl.unmarshalling.PredefinedFromStringUnmarshallers._
import akka.util.ByteString
import scala.util.parsing.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future, Await}
import scala.util.{Failure, Success}

/**
  * Created by Michael Wang on 04/26/2018.
  */
class Client(val host:String, val port:Int) {
  val contentType = ContentTypes.`application/json`
  val envRoot = "/v1/envs/"
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def execute(command:GymApi): Unit = {
    command match {
      case c: createEnv => println("createEnv")
        val uri=host+":"+port+envRoot
        val http = HttpRequest(uri = uri).withMethod(c.method).withEntity(HttpEntity(contentType,c.source))
        val responseFuture: Future[HttpResponse] = Http().singleRequest(http)

       responseFuture  onComplete {
          case Success(response) => println(response)
            response match {
               case HttpResponse(StatusCodes.OK, headers, entity, _) =>  println(s"entity=$entity")
                 val ss = Unmarshal(entity).to[GymInstance]
                 ss onComplete {
                   case   Success(gymInstance) => println(s"instance_id=${gymInstance.instance_id}")
                   case   Failure(t) => println("An error has occurred: " + t.getMessage)
                 }
                // println(s"ss=${ss.toString}")
               case HttpResponse(code, _, entity, _) => println("status is not OK")
            }
          case  Failure(t) => println("An error has occurred: " + t.getMessage)
        }

      case c: listEnvs => println("listEnvs")
        val uri=host+":"+port+envRoot
        val http = HttpRequest(uri = uri).withMethod(c.method).withEntity(HttpEntity(contentType,""))
        val responseFuture: Future[HttpResponse] = Http().singleRequest(http)
        responseFuture  onComplete {
          case Success(response) => println(response)
            response match {
              case HttpResponse(StatusCodes.OK, headers, entity, _) =>  println(s"entity=$entity")
                val ss = Unmarshal(entity).to[GymAllEnvs]
                ss onComplete {
                  case   Success(gymAllEnvs) => println(s"instance_id=${gymAllEnvs}")
                  case   Failure(t) => println("An error has occurred: " + t.getMessage)
                }
              // println(s"ss=${ss.toString}")
              case HttpResponse(code, _, entity, _) => println("status is not OK")
            }
          case  Failure(t) => println("An error has occurred: " + t.getMessage)
        }
      case c: resetEnv => println("resetEnv")
        val uri=host+":"+port+envRoot+c.instanceId.get+"/reset/"
        val http = HttpRequest(uri = uri).withMethod(c.method).withEntity(HttpEntity(contentType,c.source))
        val responseFuture: Future[HttpResponse] = Http().singleRequest(http)
        responseFuture  onComplete {
          case Success(response) => println(response)
          case Failure(t) => println("An error has occurred: " + t.getMessage)
        }
    }
  }
}
