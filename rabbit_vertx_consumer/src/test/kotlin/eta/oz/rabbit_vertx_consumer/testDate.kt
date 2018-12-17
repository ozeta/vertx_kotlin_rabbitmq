package eta.oz.rabbit_vertx_consumer

import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.util.*


fun main(args: Array<String>) {
  val payload = Payload("uuid", 2.2, 3.3, DateTime.now())
  val event = Event(Header(Source.IOT, EventType.CREATED, UUID.randomUUID()), payload)
  val toCharArray = event.toString().toCharArray()

  val mapper = jacksonObjectMapper()
  mapper.registerModule(JodaModule())
  val str = mapper.writeValueAsString(event)
  println(str)
  val readValue = mapper.readValue<Event>(str)
  println(readValue)
}

fun main1(args: Array<String>) {
//  val string = "2018-12-16 01:26:21.557766"
  val jtdate = "2018-12-16T13:33:32+01:00"

//  val formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss.nnnnnn", Locale.ITALIAN)
  val parser2 = ISODateTimeFormat.dateTimeNoMillis()
  val date = parser2.parseDateTime(jtdate)
  println(date.toDateTimeISO())

//  println(date)
}
