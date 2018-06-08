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
import breeze.linalg._
import gym.scala.client.GymSpace.{Observation, ObservationSpace}
import gym.scala.client._
import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

import breeze.plot._


object CartPole extends App {


  val buckets = (1, 1, 6, 3)
  gymClient.host("http://127.0.0.1")
    .port(5000)
    .timeout(20)
  val createEnv = new createEnv("CartPole-v0")
  val gymInstance = gymClient.execute(createEnv)

  val aSpace = actionSpace(gymInstance)
  val gymActionSpace = gymClient.execute(aSpace)

  implicit val obsspace = obsSpace(gymInstance)
  val gymObsSpace = gymClient.execute(obsspace)
  train
  //run

  def run = {
    val thePolicy = cartPolePolicy(buckets._1 * buckets._2 * buckets._3 * buckets._4, gymActionSpace.info.n)
    thePolicy load "c://work/tmp/cartpole"
    println(thePolicy.q)
    val reset = resetEnv(gymInstance)
    var gymObs = gymClient.execute(reset)
    var origObs = gymObsSpace.discretize(gymObs, buckets)
    for (episode <- 500 to 600) {
      var done = false
      thePolicy.setEpisode(episode)
      origObs = gymObsSpace.discretize(gymClient.execute(reset), buckets)
      for (t <- 1 to 210 if !done) {
        val action = thePolicy.chooseAction(origObs.indice)
        val step1 = step(gymInstance, action, true)
        val stepReply = gymClient.execute(step1)
        done = stepReply.done
        val next_obs = gymObsSpace.discretize(Observation(stepReply.observation), buckets)
        thePolicy.update(origObs.indice, next_obs.indice, action, stepReply.reward)
        origObs = next_obs
        if (done) {
          println(s"reward of episode ${episode} =${t}")
        }
      }
    }
    thePolicy save "c://work/tmp/cartpole"
    val shutDown = shutdown()
    gymClient.execute(shutDown)
    gymClient.terminate
  }
  def train = {
    val thePolicy = cartPolePolicy(buckets._1 * buckets._2 * buckets._3 * buckets._4, gymActionSpace.info.n)
    println(thePolicy)
    val reset = resetEnv(gymInstance)
    var gymObs = gymClient.execute(reset)
    println(gymObs)
    val cartPoleObsSpace:CartPoleObservationSpace = gymObsSpace
    var origObs = cartPoleObsSpace.discretize(gymObs, buckets)
    var rewards  = List[Double]()
    for (episode <- 1 to 500) {
      var done = false
      thePolicy.setEpisode(episode)
      origObs = cartPoleObsSpace.discretize(gymClient.execute(reset), buckets)
      for (t <- 1 to 210 if !done) {
        val action = thePolicy.chooseAction(origObs.indice)
        val step1 = step(gymInstance, action, false)
        val stepReply = gymClient.execute(step1)
        done = stepReply.done
        val next_obs = cartPoleObsSpace.discretize(Observation(stepReply.observation), buckets)
        thePolicy.update(origObs.indice, next_obs.indice, action, stepReply.reward)
        origObs = next_obs
        if (done) rewards = t :: rewards
      }
    }
    thePolicy save "c://work/tmp/cartpole"
    val shutDown = shutdown()
    gymClient.execute(shutDown)

    gymClient.terminate
    val rewardsVector:DenseVector[Double] = DenseVector(rewards.reverse.toArray)
    val f0 = Figure()
    val p0 = f0.subplot(0)
    p0 += plot(linspace(0, rewards.size, rewards.size), rewardsVector,  name="Rewards")

  }


  case class CartPoleObservation(x: Int, xDot: Int, theta: Int, thetaDot: Int) {
    def indice = ((x*buckets._2 + xDot) * buckets._3 + theta )*buckets._4 + thetaDot
  }

  object CartPoleObservation {
    def apply(indice:Int) = {
      val thetadot = indice % buckets._4
      val tmp1 = (indice-thetadot)/buckets._4
      val theta = tmp1%buckets._3
      val tmp2 = (tmp1-theta)/buckets._3
      val xdot = tmp2%buckets._2
      val tmp3 = (tmp2 - xdot)/buckets._2
      val x = tmp3/buckets._1
      new CartPoleObservation(x, xdot, theta, thetadot)
    }
  }
  case class CartPoleObservationSpace(high:(Double, Double, Double, Double), low:(Double, Double, Double, Double), name:String="", shape:Int=4) {
    def discretize(obs:Observation, buckets:(Int, Int, Int, Int)): Observation  = {
      val ratios = ((obs.observation(0) + scala.math.abs(low._1)) / (high._1 - low._1),
        (obs.observation(1) + scala.math.abs(low._2)) / (high._2 - low._2),
        (obs.observation(2) + scala.math.abs(low._3)) / (high._3 - low._3),
        (obs.observation(3) + scala.math.abs(low._4)) / (high._4 - low._4))
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
  implicit def gymObsSpace2CartPoleObsSpace(obsSpace: ObservationSpace):CartPoleObservationSpace = {
    val high = (obsSpace.info.high(0), 0.5, obsSpace.info.high(2), scala.math.toRadians(50.0))
    val low = (obsSpace.info.low(0), -0.5, obsSpace.info.low(2), scala.math.toRadians(-50.0))
    val name = obsSpace.info.name
    val shape = obsSpace.info.shape(0)
    CartPoleObservationSpace(high, low, name, shape)

  }
  implicit def gymObs2Observation(gymObs:Observation):CartPoleObservation = {
    new CartPoleObservation(gymObs.observation(0).toInt,
      gymObs.observation(1).toInt,
      gymObs.observation(2).toInt,
      gymObs.observation(3).toInt)
  }

  case class cartPolePolicy ( indices:Int, actions:Int) {
    val discount = 0.99
    var episode = 0
    def setEpisode(epi:Int) = {
      episode = epi
    }
    var q = DenseMatrix.zeros[Double](indices, actions)
    def update(old_indice:Int, new_indice:Int, action:Int, reward:Int):Unit = {
      q(old_indice, action) = q(old_indice,action) + learning_rate * (reward + discount * max(q(new_indice, ::)) - q(old_indice, action))
    }
    def reset = q = DenseMatrix.zeros[Double](indices, actions)
    def chooseAction(indice:Int) = {
      if (scala.math.random <= explore_rate ) gymActionSpace.sample else argmax(q(indice,::))
    }
    def explore_rate = {
      scala.math.max(0.01, scala.math.min(1, 1.0 - scala.math.log10((episode +1 )/25)))
    }
    def learning_rate = {
      scala.math.max(0.01, scala.math.min(0.5, 1.0 - scala.math.log10((episode + 1) / 25)))
    }
    def save(file:String) = {
      val oos = new ObjectOutputStream(new FileOutputStream(file))
      oos.writeObject(q)
      oos.close
    }
    def load(file:String)= {
      val ois = new ObjectInputStream(new FileInputStream(file))
      q = ois.readObject.asInstanceOf[DenseMatrix[Double]]
      ois.close
    }
  }
}


