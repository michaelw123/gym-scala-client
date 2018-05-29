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
import breeze.linalg.DenseMatrix
import gym.scala.client._
import gym.scala.client.GymSpace.{StepReply, _}
object CartPole extends App {
  val buckets = (1, 1, 6, 12)
  gymClient.host("http://127.0.0.1")
    .port(5000)
    .timeout(20)
  val createEnv = new createEnv("CartPole-v0")
  val gymInstance = gymClient.execute(createEnv)

  val aSpace = actionSpace(gymInstance)
  val gymActionSpace = gymClient.execute(aSpace)

  implicit val obsspace = obsSpace(gymInstance)
  val gymObsSpace = gymClient.execute(obsspace)

  val thePolicy = cartPolePolicy(buckets._1 * buckets._2 * buckets._3 * buckets._4, gymActionSpace.info.n)
  println(thePolicy)
  val reset = resetEnv(gymInstance)
  val gymObs = gymClient.execute(reset)
  println(gymObs)
  val newObs = gymObsSpace.discretize(gymObs, buckets)
  gymActionSpace.sample
  printit(newObs)
  for (j <- 1 to 100) {
  for (i <- 1 to 100) {
    val step1 = step(gymInstance, gymActionSpace.sample)
    val stepReply = gymClient.execute(step1)
    val obs =  gymObsSpace.discretize(Observation(stepReply.observation), buckets)
    println(obs)
  }
    val gymObs = gymClient.execute(reset)
}

  val y:Int = newObs.indice
  val z = CartPoleObservation(y)
  println(z)

  //save policy - ticktacktoe

  val shutDown = shutdown()
  gymClient.execute(shutDown)

  gymClient.terminate

  case class CartPoleObservation(x: Int, xDot: Int, theta: Int, thetaDot: Int) {
     //assume buckets and scripts are 1-based
     def indice = ((x*buckets._2 + xDot) * buckets._3 + theta )*buckets._4 + thetaDot
  }

  object CartPoleObservation {
    def apply(indice:Int) = {
      val thetaDot = indice % buckets._4
      val tmp1 = (indice-thetaDot)/buckets._4
      val theta = tmp1%buckets._3
      val tmp2 = (tmp1-theta)/buckets._3
      val xDot = tmp2%buckets._2
      val tmp3 = (tmp2 - xDot)/buckets._2
      val x = tmp3/buckets._1
      new CartPoleObservation(x, xDot, theta, thetaDot)
    }
  }
  implicit def gymObs2Observation(gymObs:Observation):CartPoleObservation = {
        new CartPoleObservation(gymObs.observation(0).toInt,
          gymObs.observation(1).toInt,
          gymObs.observation(2).toInt,
          gymObs.observation(3).toInt)
  }
  case class cartPolePolicy ( indices:Int, actions:Int) {
    val learning_rate = 0.9
    val explore_rate = 0.1
    val discount = 0.5
    var q:Array[Array[Double]] = Array.fill[Double] (indices, actions)(0)
    def chooseAction(indice:Int):Int  = if (scala.math.random <= explore_rate) sample else q(indice).argmax
    def update(old_indice:Int, new_indice:Int, action:Int, reward:Int):Unit = {
      q(old_indice)(action) = q(old_indice)(action) + learning_rate * (reward + discount * q(new_indice).max - q(old_indice)(action))
    }

  }
  def printit(x:CartPoleObservation ) = {
    println(x)
    val y:Int = x.indice
    println(s"indice = ${y}")
  }
}


