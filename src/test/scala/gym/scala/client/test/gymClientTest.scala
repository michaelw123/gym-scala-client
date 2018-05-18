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

import gym.scala.client._
/**
  * Created by Michael Wang on 05/01/2018.
  */
object gymClientTest extends App {
  gymClient.host("http://127.0.0.1")
    .port(5000)
    .timeout(20)
  val listEnvs = new listEnvs
  val envs = gymClient.execute(listEnvs)
  println(s"client: $envs")

  val createEnv = new createEnv("CartPole-v0")
  val gymInstance = gymClient.execute(createEnv)

  val monitorstart = monitorStart(gymInstance)
  gymClient.execute(monitorstart)

  val reset = resetEnv(gymInstance)
  val gymObs = gymClient.execute(reset)

  val info = actionSpace(gymInstance)
  val gymActionSpace = gymClient.execute(info)

  val obsspace = obsSpace(gymInstance)
  val gymObsSpace = gymClient.execute(obsspace)

 val  monitorclose = monitorClose(gymInstance)
  gymClient.execute(monitorclose)

  gymClient.terminate
}
