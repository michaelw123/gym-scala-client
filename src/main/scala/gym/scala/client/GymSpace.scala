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
import breeze.stats.distributions.Uniform
import gym.scala.client.GymSpace.GymAllEnvs.jsonFormat1
import gym.scala.client.GymSpace.GymInstance.jsonFormat1
import gym.scala.client.GymSpace.GymStepInfo.jsonFormat4

/**
  * Created by Michael Wang on 2018-05-13.
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

  case class GymObservation(observation: List[Float])

  object GymObservation extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymObservationFormat = jsonFormat1(GymObservation.apply)
  }

  case class ActionInfo(name: String, n: Int)

  object ActionInfo extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val infoFormat = jsonFormat2(ActionInfo.apply)
  }

  case class GymActionInfo(info: ActionInfo)

  object GymActionInfo extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymInfoFormat = jsonFormat1(GymActionInfo.apply)
  }

  case class ObsInfo(high: List[Double], low: List[Double], name: String, shape: List[Int])

  object ObsInfo extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val infoFormat = jsonFormat4(ObsInfo.apply)
  }

  case class GymObsInfo(info: ObsInfo)

  object GymObsInfo extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymInfoFormat = jsonFormat1(GymObsInfo.apply)
  }

  case class GymStepInfo(observation: List[Double], reward: Float, done: Boolean, info: Map[String, String])

  object GymStepInfo extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymStepFormat = jsonFormat4(GymStepInfo.apply)
  }

}
object GymSpace1 {

  trait Space
  case class DiscreteSpace(name: String, n: Int) extends Space{
    def randomAction:Int = {
      val r = scala.util.Random
      r.nextInt(n)
    }
  }
  object DiscreteSpace  extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val discreteSpaceFormat = jsonFormat2(DiscreteSpace.apply)
  }
  case class BoxSpace(high: List[Double], low: List[Double], name: String, shape: List[Int])
  object BoxSpace  extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val boxSpaceFormat = jsonFormat4(BoxSpace.apply)
  }

  class ActionSpace[S <: DiscreteSpace](space:S)  {
    def randomAction:Int = space.randomAction
  }

  case class Observation(observation: List[Float])
  object Observation extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val observationFormat = jsonFormat1(Observation.apply)
  }

  case class ObservationSpace[S <:BoxSpace](space:S)
  object ObservationSpace


  case class GymInstance(instance_id: String)

  object GymInstance extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymInstanceFormat = jsonFormat1(GymInstance.apply)
  }

  case class GymAllEnvs(all_envs: Map[String, String])

  object GymAllEnvs extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymAllEnvsFormat = jsonFormat1(GymAllEnvs.apply)
  }

  case class StepReply(observation: Observation, reward: Float, done: Boolean, info: Map[String, String])

  object GymStepInfo extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val gymStepFormat = jsonFormat4(StepReply.apply)
  }
}