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

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by Michael Wang on 2018-05-13.
  */
case class GymInstance(instance_id:String)
object GymInstance extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val gymInstanceFormat = jsonFormat1(GymInstance.apply)
}
case class GymAllEnvs(all_envs:Map[String, String])
object GymAllEnvs extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val gymAllEnvsFormat = jsonFormat1(GymAllEnvs.apply)
}
case class GymObservation (observation:List[Float])
object GymObservation extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val gymObservationFormat = jsonFormat1(GymObservation.apply)
}