package gym.scala.client.test

import gym.scala.client._
/**
  * Created by wangmich on 05/01/2018.
  */
object gymClientTest extends App {

   val client = new Client("http://127.0.0.1", 5000)
   val createEnv = new createEnv(None, "CartPole-v0")
   client.execute(createEnv)

}
