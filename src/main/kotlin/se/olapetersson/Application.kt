package se.olapetersson

import io.vertx.core.Vertx
import io.vertx.ext.web.Router.router
import se.olapetersson.covfefe.BeansVerticle

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val httpServer = vertx.createHttpServer()

    val router = router(vertx)

    val movieSessionVerticle = BeansVerticle()
    val beansRoutes = movieSessionVerticle.registerRoutes(vertx)


    router.route("/").handler { routingContext ->
        val response = routingContext.response()
        response.putHeader("content-type", "text-plain")
        response.setChunked(true).end("Root context page")
    }
    router.mountSubRouter("/beans", beansRoutes)

    vertx.deployVerticle(movieSessionVerticle)


    val port = 8008
    httpServer.requestHandler(router::accept).listen(port)

    println("Server started and is listening on $port")
}

