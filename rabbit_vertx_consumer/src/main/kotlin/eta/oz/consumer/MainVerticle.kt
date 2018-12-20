package eta.oz.consumer

import eta.oz.consumer.verticle.RabbitConsumer
import eta.oz.consumer.verticle.SqlService
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.json.JsonObject

class MainVerticle : AbstractVerticle() {

  override fun start(startFuture: Future<Void>) {
    print(banner)
    val conf = JsonObject()
    conf.put("eventbus.name", "io.iot")

    conf.put("rabbit.host", "192.168.1.42")
    conf.put("rabbit.port", 5672)
    conf.put("rabbit.queue", "iot.poc")
    conf.put("rabbit.username", "mqtt")
    conf.put("rabbit.password", "mqtt")

//pihole
    conf.put("mysql.host", "192.168.1.42")
    conf.put("mysql.port", 3306)
    conf.put("mysql.db", "iot")
    conf.put("mysql.table", "sessions_test")
    conf.put("mysql.username", "root")
    conf.put("mysql.password", "root")


/*
    //localhost
    conf.put("mysql.host", "localhost")
    conf.put("mysql.port", 3306)
    conf.put("mysql.db", "iot")
    conf.put("mysql.table", "sessions_test")
    conf.put("mysql.username", "root")
*/
    val options = DeploymentOptions()
      .setConfig(conf)
      .setInstances(1)
    var eb = vertx.eventBus()

    vertx.deployVerticle(SqlService(), options) { res ->
       if (res.succeeded()) {
         println("Deployment id is: ${res.result()}")
       } else {
         println("Deployment failed!")
       }
     }
//    vertx.deployVerticle(SqlService())


     vertx.deployVerticle(RabbitConsumer(), options) { res ->
       if (res.succeeded()) {
         println("Deployment id is: ${res.result()}")
       } else {
         println("Deployment failed!")
       }
     }



  }
}
