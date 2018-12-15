package eta.oz.rabbit.rabbit_vertx.application.verticle.rabbit

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.rabbitmq.RabbitMQClient
import io.vertx.core.json.JsonObject
import io.vertx.rabbitmq.RabbitMQOptions
import java.util.*


public class RabbitClientVerticle() : AbstractVerticle() {
  lateinit var client: RabbitMQClient
  override fun start() {
    println("Rabbit started")
    var config = RabbitMQOptions()
    config.host = "192.168.1.42"
    config.port = 5672
    config.user = "mqtt"
    config.password = "mqtt"

    val client = RabbitMQClient.create(vertx, config)
    val message = JsonObject().put("body", "Hello RabbitMQ, from Vert.x ! " + Date())

    client.start {
      client.basicPublish("", "hello", message) { pubResult ->
        if (pubResult.succeeded()) {
          println("Message published !")
        } else {
          pubResult.cause().printStackTrace()
        }
      }
    }

  }

  override fun stop() {
    client.stop {
      println("Rabbit stopped")

    }
  }
}
