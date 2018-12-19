package eta.oz.consumer.data

import io.vertx.core.json.JsonObject
//import org.joda.time.DateTime
//import org.joda.time.format.ISODateTimeFormat
import java.util.*

enum class Source {
  IOT
}

enum class EventType {
  RECEIVED,
  CREATED,
  FORWARDED
}

data class Header(val source: Source, val eventType: EventType, val traceId: UUID)
data class Payload(val device_uuid: String, val temp: Double, val hum: Double, val date: String) {
  fun toJson(): JsonObject {
    return JsonObject()
      .put("device_uuid", this.device_uuid)
      .put("date", this.date)
      .put("hum", this.hum)
      .put("temp", this.temp)
  }
}

data class Event(val header: Header, val body: Payload)
