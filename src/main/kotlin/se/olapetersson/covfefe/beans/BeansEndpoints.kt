package se.olapetersson.covfefe.beans

import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

class BeansEndpoints(private val vertx: Vertx) {

    val logger = LoggerFactory.getLogger(BeansEndpoints::class.java.name)
    val eventBus = vertx.eventBus()!!

    fun registerRoutes(): Router {
        val router = Router.router(vertx)
        val postRoute = router.post("/:name").handler { routingContext ->
            val beanName = routingContext.request().getParam("name")

            val beanRepositoryMessage = json {
                obj("name" to "$beanName"
                )
            }

            logger.info("Received a $beanName to create")

            eventBus.addCoffeeBean(AddBeanRequest(beanName)) {
                logger.info("Received reply in POST $it")
                val response = routingContext.response()
                response.putHeader("content-type", "application/json")

                response.setChunked(true).end("created $beanName")
            }
        }

        val getRoute = router.get("/").handler { routingContext ->
            eventBus.send<String>(BeansRepository.ADDRESS_READ, "") { reply ->
                logger.info("Received reply in GET ${reply.result()?.body()}")
                val response = routingContext.response()
                response.putHeader("content-type", "application/json")
                if (!reply.succeeded()) {
                    throw IllegalStateException(reply.cause()) as Throwable
                }
                response.setChunked(true).end(reply.result().body().toString())
            }
        }

        return router
    }
}