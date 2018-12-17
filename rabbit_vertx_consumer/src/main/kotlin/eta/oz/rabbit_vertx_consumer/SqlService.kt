package eta.oz.rabbit_vertx_consumer

import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.ext.asyncsql.AsyncSQLClient
import io.vertx.ext.asyncsql.MySQLClient
import io.vertx.kotlin.core.json.*


class SqlService : AbstractVerticle() {
  lateinit var eb: EventBus
  lateinit var mySQLClient: AsyncSQLClient

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
                obj(mapOf(
                  "id" to it.getLong(0),
                  "payload" to it.getString(1),
                  "device_uuid" to it.getString(2)
                ))
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

  private fun insert(uuid: String = "iot_clima", body: String = """{"ciao":"bao"}""") {
    mySQLClient.update("INSERT INTO sessions (payload, device_uuid) VALUES ('$body', '$uuid')") { res ->
      if (res.succeeded()) {

        var result = res.result()
        println("Updated no. of rows: ${result.updated}")
        println("Generated keys: ${result.keys}")

      } else {
        println(res.cause().message)
      }
    }
  }

  override fun start() {
    eb = vertx.eventBus()
    eb.consumer<Event>("io.iot") { message ->
      if (message.body() is Event) {

        println("I have received a message: ${message.body()}")
        val json = json {
          obj("date" to message.body().body.date,
            "hum (%)" to message.body().body.hum,
            "temp (C)" to message.body().body.temp
          )
        }
        vertx.executeBlocking<Any>({ future ->
          insert(message.body().body.device_uuid, json.toString())
          var result = message.body().header.traceId.toString()
          future.complete(result)
        }, { res ->
          println("message saved: ${res.result()}")

        })
      }
    }

    var mySQLClientConfig = json {
      obj(mapOf(
        "host" to "192.168.1.42",
        "port" to 3306,
        "username" to "root",
        "password" to "root",
        "database" to "iot"
      ))
    }
    mySQLClient = MySQLClient.createShared(vertx, mySQLClientConfig)
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
}
