# gym-scala-client

A scala RESTful client for openai gym that allows Scala to have an access to openai gym through gym-http-api using akka-http, spray-json, breeze, breeze-viz

gym http api specification:
https://github.com/openai/gym-http-api/blob/master/README.md

Following is a list of HTTP requests that this API supports:

POST /v1/envs/

GET /v1/envs/

POST /v1/envs/<instance_id>/reset/

POST /v1/envs/<instance_id>/step/

GET /v1/envs/<instance_id>/action_space/

GET /v1/envs/<instance_id>/observation_space/

POST /v1/envs/<instance_id>/monitor/start/

POST /v1/envs/<instance_id>/monitor/close/

POST /v1/upload/

POST /v1/shutdown/

A solution of CartPole has been implemented in Scala using Q-learning through gym-scala-client. Contributions of solution to other gym environments are welcome.

![CartPole Rewards](CartPole.png?raw=true "Rewards")

![CartPole Balance](CartPole-balance.PNG?raw=true "Balance")

Please cite gym-scala-client if it helps your Scala AI project!



