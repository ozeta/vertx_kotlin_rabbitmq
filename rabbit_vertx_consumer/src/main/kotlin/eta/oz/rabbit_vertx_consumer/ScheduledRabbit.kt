package eta.oz.rabbit_vertx_consumer

import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.rabbitmq.QueueOptions
import io.vertx.rabbitmq.RabbitMQClient
import java.util.*
import io.vertx.rabbitmq.RabbitMQOptions
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat


class RabbitConsumer : AbstractVerticle() {
  val parser2: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()
  lateinit var eb: EventBus

  override fun start() {
    val config = RabbitMQOptions()
    config.host = config().getString("host", "localhost")
    config.port = config().getInteger("port", 5672)
    config.user = config().getString("user", "mqtt")
    config.password = config().getString("password", "mqtt")
    val client = RabbitMQClient.create(vertx, config)

    client.start {
      eb = vertx.eventBus()
      basicConsumer(client)
    }

  }

  private fun basicConsumer(client: RabbitMQClient) {
    val optionJson = JsonObject().put("maxInternalQueueSize", 1000)
    optionJson.put("keepMostRecent", true)
    optionJson.put("autoAck", true)

    var options = QueueOptions(optionJson)
    client.basicConsumer("iot.test", options) { rabbitMQConsumerAsyncResult ->
      if (rabbitMQConsumerAsyncResult.succeeded()) {
        var mqConsumer = rabbitMQConsumerAsyncResult.result()
        mqConsumer.handler { message ->
          var jsonMessage = message.body().toJsonObject()
          val payload = parseJsonMessage(jsonMessage)
          val event = Event(Header(Source.IOT, EventType.CREATED, UUID.randomUUID()), payload)
          eb.publish("iot", event)
          println(payload)
        }
      } else {
        rabbitMQConsumerAsyncResult.cause().printStackTrace()
      }
    }

  }


  private fun parseJsonMessage(jsonMessage: JsonObject): Payload {
    val date = jsonMessage.getString("date")
    val jodaDate = parser2.parseDateTime(date)
    val temp = jsonMessage.getDouble("temp (C)")
    val hum = jsonMessage.getDouble("hum (%)")
    val deviceUuid = jsonMessage.getString("device_uuid")
    return Payload(deviceUuid?:"iot_clima", temp, hum, jodaDate)
  }

  override fun stop() {}
}
