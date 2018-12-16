package eta.oz.rabbit_vertx_consumer

import io.vertx.core.AbstractVerticle
import io.vertx.ext.asyncsql.AsyncSQLClient
import io.vertx.ext.asyncsql.MySQLClient
import io.vertx.kotlin.core.json.*


class SqlService : AbstractVerticle() {

  fun getAllStream() {
    mySQLClient.queryStream("SELECT * FROM sessions") { stream ->
      if (stream.succeeded()) {
        stream.result().handler { row ->
          print(row)
        }
      }
    }
  }

  fun getAll() {
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

  fun insert() {
    val uuid = "iot_clima"
    val body = """{"ciao":"bao"}"""
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

  lateinit var mySQLClient: AsyncSQLClient
  override fun start() {

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
    insert()
    getAllStream()
    getAll()
  }
}
