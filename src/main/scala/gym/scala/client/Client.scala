package gym.scala.client

import spray.json._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, _}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.unmarshalling.PredefinedFromStringUnmarshallers._




import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
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
        val source = """{ "env_id": "CartPole-v0" }"""
        val http = HttpRequest(uri = uri).withMethod(HttpMethods.POST).withEntity(HttpEntity(contentType,source))
        val responseFuture: Future[HttpResponse] = Http().singleRequest(http)
        responseFuture map {
          case response @ HttpResponse(StatusCodes.OK, _, _, _) =>
         //   implicit val longFromStringUnmarshaller: Unmarshaller[String, Long]
          println(response)
            val entity = response.entity
            val l = longFromStringUnmarshaller.unmarshal("aaa:bbb", executionContext, materializer)
            response.discardEntityBytes()
          case response @ HttpResponse(code, _, _, _) => sys.error("something went wrong")
            response.discardEntityBytes()
        }
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
