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
package gym.scala.client.test

/**
  * Created by Michael Wang on 05/25/2018.
  */
import gym.scala.client._
import gym.scala.client.GymSpace._
object CartPole extends App {
  gymClient.host("http://127.0.0.1")
    .port(5000)
    .timeout(20)
  val createEnv = new createEnv("CartPole-v0")
  val gymInstance = gymClient.execute(createEnv)

  val aSpace = actionSpace(gymInstance)
  val gymActionSpace = gymClient.execute(aSpace)

  implicit val obsspace = obsSpace(gymInstance)
  val gymObsSpace = gymClient.execute(obsspace)

  val reset = resetEnv(gymInstance)
  val gymObs = gymClient.execute(reset)
  println(gymObs)

  val newObs = gymObsSpace.discretize(gymObs)
  println(newObs)

 // val newObs1:CartPoleObservation = gymObs
  printit(newObs)

  val  shutDown = shutdown()
  gymClient.execute(shutDown)

  gymClient.terminate

  case class CartPoleObservation(x:Int, xDot:Int, theta:Int, thetaDot:Int)
  implicit def gymObs2Observation(gymObs:Observation):CartPoleObservation = {
        new CartPoleObservation(gymObs.observation(0).toInt,
          gymObs.observation(1).toInt,
          gymObs.observation(2).toInt,
          gymObs.observation(3).toInt)
  }
//  implicit def gymObs2Observation(gymObs:Observation)(implicit gybObsSpace:ObservationSpace):CartPoleObservation = {
//    val obs = gybObsSpace.discretize(gymObs)
//    new CartPoleObservation(obs.observation(0).toInt,
//      obs.observation(1).toInt,
//      obs.observation(2).toInt,
//      obs.observation(3).toInt)
//  }

  def printit(x:CartPoleObservation) = println(x)
}


