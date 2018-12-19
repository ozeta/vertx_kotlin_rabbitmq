package eta.oz.consumer.verticle

//import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eta.oz.consumer.data.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.rabbitmq.QueueOptions
import io.vertx.rabbitmq.RabbitMQClient
import io.vertx.rabbitmq.RabbitMQOptions
//import org.joda.time.format.DateTimeFormatter
//import org.joda.time.format.ISODateTimeFormat
import java.util.*
import kotlin.collections.HashMap


class RabbitConsumer : AbstractVerticle() {
//  val parser2: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()
  var confMap = HashMap<String, Any>()
  lateinit var eb: EventBus
  val mapper = jacksonObjectMapper()/*.registerModule(JodaModule())*/

  override fun start() {
    val config = RabbitMQOptions()
    config.host = config().getString("rabbit.host")
    config.port = config().getInteger("rabbit.port")
    config.user = config().getString("rabbit.username")
    config.password = config().getString("rabbit.password")
    confMap["rabbit.queue"] = config().getString("rabbit.queue")
    confMap["eventbus.name"] = config().getString("eventbus.name")
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

    val options = QueueOptions(optionJson)
    client.basicConsumer(confMap["rabbit.queue"] as String, options) { asyncResult ->
      if (asyncResult.succeeded()) {
        val mqConsumer = asyncResult.result()

        mqConsumer.handler { message ->
          val jsonMessage = message.body().toJsonObject()
          val payload = parseJsonMessage(jsonMessage)
          val event = Event(Header(Source.IOT, EventType.CREATED, UUID.randomUUID()), payload)
          val eventStr = mapper.writeValueAsString(event)
          eb.publish(confMap["eventbus.name"] as String, eventStr)
//          println(payload)
        }
      } else {
        asyncResult.cause().printStackTrace()
      }
    }

  }


  private fun parseJsonMessage(jsonMessage: JsonObject): Payload {
    val date = jsonMessage.getString("date")
    val temp = jsonMessage.getDouble("temp (C)")
    val hum = jsonMessage.getDouble("hum (%)")
    val deviceUuid = jsonMessage.getString("device_uuid")
    return Payload(deviceUuid ?: "iot_clima", temp, hum, date)
  }

  override fun stop() {}
}
