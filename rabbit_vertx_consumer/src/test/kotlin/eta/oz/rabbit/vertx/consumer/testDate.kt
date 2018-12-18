package eta.oz.rabbit.vertx.consumer

import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import eta.oz.consumer.data.*
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.util.*


fun main(args: Array<String>) {



  var mySQLClientConfig = json {
    obj(
      "host" to "192.168.1.42",
      "port" to 3306,
      "username" to "root",
      "password" to "root",
      "database" to "iot"
    )
  }

  print("json: $mySQLClientConfig")




  val payload = Payload("uuid", 2.2, 3.3, DateTime.now())
  val event = Event(Header(Source.IOT, EventType.CREATED, UUID.randomUUID()), payload)
  val toCharArray = event.toString().toCharArray()

  val mapper = jacksonObjectMapper().registerModule(JodaModule())
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
