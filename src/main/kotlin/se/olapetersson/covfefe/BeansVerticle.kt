package se.olapetersson.covfefe

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.Router

class BeansVerticle : AbstractVerticle() {


    fun registerRoutes(vertx: Vertx): Router {
        val router = Router.router(vertx)
        val postRoute = router.post().handler { routingContext ->
            val response = routingContext.response()
            response.putHeader("content-type", "application/json")
            response.setChunked(true).end(mapOf("route" to "CoffeeBean POST route").toString())
        }

        val getRoute = router.get("/").handler { routingContext ->
            val response = routingContext.response()

            response.putHeader("content-type", "application/json")
            response.setChunked(true).end(mapOf( "route" to "CoffeeBean GET route").toString())
        }


        return router
    }
}