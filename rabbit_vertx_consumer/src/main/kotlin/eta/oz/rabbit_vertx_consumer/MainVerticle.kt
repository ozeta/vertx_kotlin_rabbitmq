package eta.oz.rabbit_vertx_consumer

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.asyncsql.MySQLClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

class MainVerticle : AbstractVerticle() {

  override fun start(startFuture: Future<Void>) {
    val conf = JsonObject()
    conf.put("host", "192.168.1.42")
    conf.put("port", 5672)
    val options = DeploymentOptions()
      .setConfig(conf)
      .setInstances(1)

    vertx.deployVerticle(SqlService())
    /*
     vertx.deployVerticle(ScheduledRabbitConsumer(), options) { res ->
       if (res.succeeded()) {
         println("Deployment id is: ${res.result()}")
       } else {
         println("Deployment failed!")
       }
     }
    */


  }
}
