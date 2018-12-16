package eta.oz.rabbit_vertx_consumer

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.rabbitmq.QueueOptions
import io.vertx.rabbitmq.RabbitMQClient
import java.util.*
import io.vertx.rabbitmq.RabbitMQOptions
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat


class ScheduledRabbitConsumer : AbstractVerticle() {
  val parser2: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

  override fun start() {
    val producerInterval = 1000L
    val consumerInterval = 5000L
    val config = RabbitMQOptions()
    config.host = config().getString("host", "localhost")
    config.port = config().getInteger("port", 5672)
    config.user = config().getString("user", "mqtt")
    config.password = config().getString("password", "mqtt")
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
          var jsonMessage = message.body().toJsonObject()
          parseJsonMessage(jsonMessage)
        }
      } else {
        rabbitMQConsumerAsyncResult.cause().printStackTrace()
      }
    }

  }

  private fun parseJsonMessage(jsonMessage: JsonObject) {
    val date = jsonMessage.getString("date")
    val temp = jsonMessage.getDouble("temp (C)")
    val hum = jsonMessage.getDouble("hum (%)")
    val jodaDate = parser2.parseDateTime(date)

    println("date: $jodaDate; temp: $temp; hum: $hum")
  }

  override fun stop() {}
}
