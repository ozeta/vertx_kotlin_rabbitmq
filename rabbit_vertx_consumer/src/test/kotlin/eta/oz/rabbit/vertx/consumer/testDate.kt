package eta.oz.rabbit.vertx.consumer

import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*


fun main3(args: Array<String>) {
  val str = """{"device_uuid":"iot_clima","temp":0.0,"hum":0.0,"date":1545166243000}"""
  println(str)
//  val payload = Payload("uuid", 2.2, 3.3, DateTime.now())
//  println(payload.toJson())

}

fun main2(args: Array<String>) {


  var mySQLClientConfig = json {
    obj(
      "host" to "192.168.1.42",
      "port" to 3306,
      "username" to "root",
      "password" to "root",
      "database" to "iot"
    )
  }

  println("json: $mySQLClientConfig")


//  val payload = Payload("uuid", 2.2, 3.3, DateTime.now())
//  val event = Event(Header(Source.IOT, EventType.CREATED, UUID.randomUUID()), payload)
//  val toCharArray = event.toString().toCharArray()
//
//  val mapper = jacksonObjectMapper().registerModule(JodaModule())
//  val str = mapper.writeValueAsString(event)
//  println(str)
//  val readValue = mapper.readValue<Event>(str)
//  println(readValue)
}

fun main(args: Array<String>) {

  val listOf = listOf(
    "2018-12-16T13:33:32+01:00",
    "2018-12-19T00:15:20+01:00",
    "2018-12-16T13:33:32.000+01:00",
    "2018-12-18T22:56:14.000Z")
//  val jtdate = "2018-12-18T22:56:14.000Z"

//  val formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss.nnnnnn", Locale.ITALIAN)
//  val parser2 = ISODateTimeFormat.dateTimeNoMillis()
//  val date = parser2.parseDateTime(jtdate)
//  println(date.toDateTimeISO())
//  val parse = DateTime.parse(jtdate1)
//  val parse1 = DateTime.parse(jtdate)
//  val parse2 = DateTime.parse(jtdate)
//  println(parse.toDateTimeISO().toString())
//  println(parse1.toDateTimeISO().toString())
//  println(parse2.toDateTimeISO().toString())
  val formatter = DateTimeFormatterBuilder()
    // date/time
    .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    // offset (hh:mm - "+00:00" when it's zero)
    .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
    // offset (hhmm - "+0000" when it's zero)
    .optionalStart().appendOffset("+HHMM", "+0000").optionalEnd()
    // offset (hh - "Z" when it's zero)
    .optionalStart().appendOffset("+HH", "Z").optionalEnd()
    // create formatter
    .toFormatter()
  var list = LinkedList<Any>()
  listOf.forEach {
    val parse = OffsetDateTime.parse(it, formatter)
    list.add(parse)
    println(parse.format(DateTimeFormatter.ISO_DATE_TIME))

  }

//  println(date)
}
