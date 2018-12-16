package eta.oz.rabbit_vertx_consumer

import io.vertx.ext.asyncsql.MySQLClient
import org.joda.time.format.ISODateTimeFormat
import java.sql.DriverManager
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import io.vertx.kotlin.core.json.*


fun main1(args: Array<String>) {
//  val string = "2018-12-16 01:26:21.557766"
  val jtdate = "2018-12-16T13:33:32+01:00"

//  val formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss.nnnnnn", Locale.ITALIAN)
  val parser2 = ISODateTimeFormat.dateTimeNoMillis()
  val date = parser2.parseDateTime(jtdate)
  println(date.toDateTimeISO())

//  println(date)
}
