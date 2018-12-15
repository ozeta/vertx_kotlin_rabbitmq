package eta.oz.rabbit_vertx_consumer

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.rabbitmq.QueueOptions
import io.vertx.rabbitmq.RabbitMQClient
import java.util.*
import io.vertx.rabbitmq.RabbitMQOptions


class ScheduledRabbitConsumer : AbstractVerticle() {

  override fun start() {
    val producerInterval = 1000L
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
      basicConsumer(client)
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

  override fun stop() {}
}
