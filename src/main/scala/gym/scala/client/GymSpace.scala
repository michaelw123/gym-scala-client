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
  *
  * objects unmarshalled from json string from Gym
  */
object GymSpace {

  case class GymInstance(instance_id: String)

  object GymInstance extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymInstanceFormat = jsonFormat1(GymInstance.apply)
  }

  case class GymAllEnvs(all_envs: Map[String, String])

  object GymAllEnvs extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymAllEnvsFormat = jsonFormat1(GymAllEnvs.apply)
  }

  trait Space[A] {
    def sample:A
    def contains(x:A):Boolean
  }
  case class DiscreteSpace(name: String, n: Int) extends Space[Int]{
    def sample:Int = {
      val r = scala.util.Random
      r.nextInt(n)
    }
    def contains(x:Int):Boolean = x>0 && x<n

  }
  object DiscreteSpace extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val discreteSpaceFormat = jsonFormat2(DiscreteSpace.apply)
  }
  case class ActionSpace(info: DiscreteSpace) {
    def sample:Int = info.sample
  }

  object ActionSpace extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val actionSpaceFormat = jsonFormat1(ActionSpace.apply)
  }
  case class BoxSpace(high: List[Double], low: List[Double], name: String, shape: List[Int]) extends Space[List[Double]] {
    def sample:List[Double] = {
      import breeze.stats.distributions._
      var aSample = List[Double]()
      for (i <- shape(0)-1 to 0) aSample = Uniform(low(i), high(i)).draw :: aSample
      aSample
    }
    def contains(x:List[Double]):Boolean = {
      false
    }
  }
  object BoxSpace  extends DefaultJsonProtocol with SprayJsonSupport {

    implicit val boxSpaceFormat = jsonFormat4(BoxSpace.apply)
  }

  case class Observation(observation: List[Double])
  object Observation extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val observationFormat = jsonFormat1(Observation.apply)
  }

  case class ObservationSpace (info:BoxSpace){
    def discretize(obs:Observation, buckets:(Int, Int, Int, Int)): Observation  = {
      val upperBound = (info.high(0), 0.5, info.high(2), scala.math.toRadians(50.0))
      val lowerbound = (info.low(0), -0.5, info.low(2), -scala.math.toRadians(50.0))
      val ratios = ((obs.observation(0) + scala.math.abs(lowerbound._1)) / (upperBound._1 - lowerbound._1),
          (obs.observation(1) + scala.math.abs(lowerbound._2)) / (upperBound._2 - lowerbound._2),
          (obs.observation(2) + scala.math.abs(lowerbound._3)) / (upperBound._3 - lowerbound._3),
          (obs.observation(3) + scala.math.abs(lowerbound._4)) / (upperBound._4 - lowerbound._4))
      val newObs = (scala.math.round((buckets._1 -1 ) * ratios._1),
          scala.math.round((buckets._2 -1 ) * ratios._2),
          scala.math.round((buckets._3 -1 ) * ratios._3),
          scala.math.round((buckets._4 -1 ) * ratios._4))
      val theObs:List[Double] = List(scala.math.min(buckets._1 -1, scala.math.max(0, newObs._1)),
        scala.math.min(buckets._2 -1, scala.math.max(0, newObs._2)),
        scala.math.min(buckets._3 -1, scala.math.max(0, newObs._3)),
        scala.math.min(buckets._4 -1, scala.math.max(0, newObs._4)))
      Observation(theObs)
    }
  }
  object  ObservationSpace extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val observationSpaceFormat = jsonFormat1(ObservationSpace.apply)
  }
  case class StepReply(observation: List[Double], reward: Int, done: Boolean, info: Map[String, String])

  object StepReply extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymStepFormat = jsonFormat4(StepReply.apply)
  }
}