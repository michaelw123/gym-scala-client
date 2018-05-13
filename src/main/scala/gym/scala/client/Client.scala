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
  case class GymInstance(instance_id:String)
  object GymInstance extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val folderFormat = jsonFormat1(GymInstance.apply)
  }


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
                   case   Success(json) => println(s"json=${json.instance_id}")
                    case   Failure(t) => println("An error has occurred: " + t.getMessage)
                 }
                // println(s"ss=${ss.toString}")
               case HttpResponse(code, _, entity, _) => println("status is not OK")
            }
          case  Failure(t) => println("An error has occurred: " + t.getMessage)
        }
      //        val result = responseFuture map {
//          case HttpResponse(StatusCodes.OK, headers, entity, _) =>
//            Unmarshal(entity).to[String]
//
//          case x => s"Unexpected status code ${x.status}"
//          _ match {
//            case HttpResponse(StatusCodes.OK, headers, entity, _) =>
//              val bytes = entity.dataBytes.runFold(ByteString())(_ ++ _)
//              bytes.onComplete {
//                case Success(value) => println(s"Got the callback, meaning = ${value.utf8String}")
//                  val asString = value.utf8String
//                  byteStringUnmarshaller.unmarshal(value)
//                  val l = longFromStringUnmarshaller.unmarshal(asString, executionContext, materializer)
//                  println(s"asLong value= $l")
//                case Failure(e) => e.printStackTrace
//              }
//              "OK"
//            case HttpResponse(code, _, entity, _) =>
//              //entity.toStrict(100, materializer)   N.B. Not having this, will freeze after 4 calls!
//              s"Response code: $code"
//            case unknown =>
//              s"unkown response: $unknown"
//          }
//        }
//        val aa = Await.result(result, 10.seconds)
//        println(s"aa=${aa.toString}")

//        val valueAsString = aa match {
//          case Success(value) => value
//          case Failure(e) => e.printStackTrace
//            "Error"
//        }
//        println(valueAsString)

//        responseFuture map {
//          case response @ HttpResponse(StatusCodes.OK, _, _, _) =>
//         //   implicit val longFromStringUnmarshaller: Unmarshaller[String, Long]
//          println(response)
//            val entity = response.entity.toStrict(3.seconds)
//            val transformedData: Future[Long] =
//              entity flatMap { e =>
//                e.dataBytes
//                  .runFold(ByteString.empty) { case (acc, b) => acc ++ b }
//                  .map(parse)
//              }
//            val l = longFromStringUnmarshaller.unmarshal("aaa:bbb", executionContext, materializer)
//            response.discardEntityBytes()
//          case response @ HttpResponse(code, _, _, _) => sys.error("something went wrong")
//            response.discardEntityBytes()
//        }
      case c: listEnvs => println("listEnvs")
        val uri=host+":"+port+envRoot
        val http = HttpRequest(uri = uri).withMethod(HttpMethods.GET).withEntity(HttpEntity(contentType,""))
        val responseFuture: Future[HttpResponse] = Http().singleRequest(http)
        responseFuture map {
          case response @ HttpResponse(StatusCodes.OK, _, _, _) =>
            println(response)
            response.discardEntityBytes()
          case response @ HttpResponse(code, _, _, _) => sys.error("something went wrong")
            response.discardEntityBytes()
        }
    }
  }

//  val jsonAst = source.parseJson // or JsonParser(source)
//  val json = jsonAst.prettyPrint
//
//  //printf(json)
//
//  implicit val system: ActorSystem = ActorSystem()
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
//
//  val http = HttpRequest(uri = "http://127.0.0.1:5000/v1/envs/")
//    .withMethod(HttpMethods.POST).withEntity(HttpEntity(ContentTypes.`application/json`,source))
//  val responseFuture: Future[HttpResponse] = Http().singleRequest(http)
//
//  responseFuture
//    .onComplete {
//      case Success(res) => println(res)
//        val e = res.entity
//      case Failure(_)   => sys.error("something wrong")
//    }
//
//  println(json)
  //val jsonAst1 = List(1, 2, 3).toJson
  //val myObject = jsonAst.convertTo[MyObjectType]
}
