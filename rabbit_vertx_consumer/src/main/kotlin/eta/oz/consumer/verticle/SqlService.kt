package eta.oz.consumer.verticle

//import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import eta.oz.consumer.data.Event
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.ext.asyncsql.AsyncSQLClient
import io.vertx.ext.asyncsql.MySQLClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj


class SqlService : AbstractVerticle() {
  lateinit var eventBus: EventBus
  lateinit var mySQLClient: AsyncSQLClient
  val mapper = jacksonObjectMapper()/*.registerModule(JodaModule())*/

  private fun getAllStream() {
    mySQLClient.queryStream("SELECT * FROM sessions") { stream ->
      if (stream.succeeded()) {
        stream.result().handler { row ->
          print(row)
        }
      }
    }
  }

  private fun getAll() {
    mySQLClient.getConnection { res ->
      if (res.succeeded()) {
        // Got a connection
        val connection = res.result()
        connection.query("SELECT * FROM sessions") { ar ->
          if (ar.succeeded()) {
            ar.result().results.forEach {
              val json = json {
                obj(
                  "id" to it.getLong(0),
                  "payload" to it.getString(1),
                  "device_uuid" to it.getString(2)
                )
              }
              println(json)
            }
          } else {
            println(ar.cause().message)
          }
        }
      } else {
        println(res.cause().message)
      }
    }
  }

  private fun insert(uuid: String = "iot_clima", body: String = """{"ktest":"val"}""") {
    val table = config().getString("mysql.table")
    mySQLClient.update("INSERT INTO $table (payload, device_uuid) VALUES ('$body', '$uuid')") { res ->
      if (res.succeeded()) {

        var result = res.result()
//        println("Updated no. of rows: ${result.updated}")
//        println("Generated keys: ${result.keys}")

      } else {
        println(res.cause().message)
      }
    }
  }

  override fun start() {
    eventBus = vertx.eventBus()
    connectSql()
    consumeFromBus()


/*    vertx.executeBlocking<Any>({ future ->
      // Call some blocking API that takes a significant amount of time to return
//      insert()
*//*      getAllStream()
      getAll()*//*
      var result = "ok"
      future.complete(result)
    }, { res ->
      println("The result is: ${res.result()}")

    })*/

  }

  private fun connectSql() {

    var mySQLClientConfig = json {
      obj(
        "host" to config().getString("mysql.host"),
        "port" to config().getInteger("mysql.port"),
        "username" to config().getString("mysql.username"),
        "password" to config().getString("mysql.password"),
        "database" to config().getString("mysql.db")
      )
    }
    this.mySQLClient = MySQLClient.createShared(vertx, mySQLClientConfig)
  }

  private fun consumeFromBus() {
    eventBus.consumer<String>(config().getString("eventbus.name")) { message ->
      val readValue = mapper.readValue<Event>(message.body())
      val body = readValue.body
      val header = readValue.header
      val jsn = body.toJson().toString()
//      println(jsn)
      vertx.executeBlocking<Any>({ future ->
        insert(body.device_uuid, jsn)
        var result = header.traceId.toString()
        future.complete(result)
      }, { res ->
        //        println("message stored to db: ${res.result()}")

      })
    }
  }
}
