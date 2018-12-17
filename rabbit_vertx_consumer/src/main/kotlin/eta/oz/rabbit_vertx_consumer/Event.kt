package eta.oz.rabbit_vertx_consumer

import java.util.*

enum class Source() {
  IOT
}

enum class EventType() {
  RECEIVED,
  CREATED,
  FORWARDED
}

data class Header(val source: Source, val eventType: EventType, val traceId: UUID)
data class Payload(val device_uuid: String, val temp: Double, val hum: Double, val date: org.joda.time.DateTime)
data class Event(val header: Header, val body: Payload)
