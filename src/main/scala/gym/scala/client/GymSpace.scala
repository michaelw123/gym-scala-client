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

import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport


/**
  * Created by Michael Wang on 2018-05-13.
  */
object GymSpace {

  case class GymInstance private (instance_id: String)

  object GymInstance extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymInstanceFormat = jsonFormat1(GymInstance.apply)
  }

  case class GymAllEnvs private(all_envs: Map[String, String])

  object GymAllEnvs extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymAllEnvsFormat = jsonFormat1(GymAllEnvs.apply)
  }

  trait Space[A] {
    def sample:A
    def contains(x:A):Boolean
  }
  case class DiscreteSpace private (name: String, n: Int) extends Space[Int]{
    def sample:Int = {
      val r = scala.util.Random
      r.nextInt(n)
    }
    def contains(x:Int):Boolean = x>0 && x<n

  }
  object DiscreteSpace extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val discreteSpaceFormat = jsonFormat2(DiscreteSpace.apply)
  }
  case class ActionSpace private (info: DiscreteSpace) {
    def sample:Int = info.sample
  }

  object ActionSpace extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val actionSpaceFormat = jsonFormat1(ActionSpace.apply)
  }
  case class BoxSpace private (high: List[Double], low: List[Double], name: String, shape: List[Int]) extends Space[List[Double]] {
    def sample:List[Double] = {
      import breeze.stats.distributions._
      List(Uniform(low(0), high(0)).draw,
        Uniform(low(1), high(1)).draw,
        Uniform(low(2), high(2)).draw,
        Uniform(low(3), high(3)).draw)
    }
    def contains(x:List[Double]):Boolean = {
      false
    }
  }
  object BoxSpace  extends DefaultJsonProtocol with SprayJsonSupport {

    implicit val boxSpaceFormat = jsonFormat4(BoxSpace.apply)
  }

  case class Observation private (observation: List[Double])
  object Observation extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val observationFormat = jsonFormat1(Observation.apply)
  }

  case class ObservationSpace private (info:BoxSpace)
  object  ObservationSpace extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val observationSpaceFormat = jsonFormat1(ObservationSpace.apply)
  }
  case class StepReply private (observation: List[Double], reward: Int, done: Boolean, info: Map[String, String])

  object StepReply extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymStepFormat = jsonFormat4(StepReply.apply)
  }
}