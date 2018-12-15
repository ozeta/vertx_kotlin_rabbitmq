package eta.oz.rabbit.rabbit_vertx.verticle.timer

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.rabbitmq.QueueOptions
import io.vertx.rabbitmq.RabbitMQClient
import java.util.*
import io.vertx.rabbitmq.RabbitMQOptions


class ScheduledRabbit : AbstractVerticle() {

  override fun start() {
    val producerInterval = 300L
    val consumerInterval = 5000L
    val config = RabbitMQOptions()
    config.host = "192.168.1.42"
    config.port = 5672
    config.user = "mqtt"
    config.password = "mqtt"
    val client = RabbitMQClient.create(vertx, config)

//    vertx.setPeriodic(interval) {
//    }
    client.start() {
      vertx.setPeriodic(producerInterval) {
        basicPublish(client)
      }
/*      vertx.setPeriodic(consumerInterval) {
        basicConsumer(client)
      }*/
    }

  }

  private fun basicConsumer(client: RabbitMQClient) {
    val optionJson = JsonObject().put("maxInternalQueueSize", 1000)
    optionJson.put("keepMostRecent", true)
    optionJson.put("autoAck", true)

    var options = QueueOptions(optionJson)
    client.basicConsumer("hello", options) { rabbitMQConsumerAsyncResult ->
      if (rabbitMQConsumerAsyncResult.succeeded()) {
        println("new messages!")
        var mqConsumer = rabbitMQConsumerAsyncResult.result()
        mqConsumer.handler { message ->
          println("Got message: ${message.body().toString()}")
        }
      } else {
        rabbitMQConsumerAsyncResult.cause().printStackTrace()
      }
    }

  }

  private fun basicPublish(client: RabbitMQClient) {

    val date = JsonObject().put("date", Date().toString())
    val data = JsonObject().put("message", "hello!")
    data.put("date", date)
    data.put("version", "0.1")
    val message = JsonObject().put("body", data.toString())
//    println(message)

    client.basicPublish("", "hello", message) { result ->
      if (result.succeeded()) {
//        println("Message published !")
      } else {
        result.cause().printStackTrace()
      }
    }
  }


  override fun stop() {}
}
