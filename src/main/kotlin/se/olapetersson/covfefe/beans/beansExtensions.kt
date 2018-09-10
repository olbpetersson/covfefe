package se.olapetersson.covfefe.beans

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import se.olapetersson.covfefe.beans.BeansRepository.Companion.ADDRESS


val logger = LoggerFactory.getLogger("BeansExtension")

fun coffeeMessageMapper(jsonString: String): BeansRequest? {
    val jsonObject = JsonObject(jsonString)
    logger.info("got json string '$jsonString'")
    val messageType = BeansMessageType.valueOf(jsonObject.getString("type"))
    return Json.decodeValue(jsonString, messageType.value)
}

fun EventBus.addCoffeeBean(addBeanRequest: AddBeanRequest, handler: (AddBeanResponse) -> Unit) {
    this.send<String>(
        BeansRepository.ADDRESS,
        Json.encode(addBeanRequest),
        se.olapetersson.covfefe.beans.unwrapPayload(handler))
}

fun EventBus.consumeCoffeeBeanRequest(handler: (AddBeanRequest, Message<String>) -> Unit) {
    this.consumer<String>(ADDRESS) { message ->
        val serializedRequest = Json.decodeValue(message.body(), AddBeanRequest::class.java)
        handler(serializedRequest, message)
    }
}

private fun <T> unwrapPayload(handler: (T) -> Unit): Handler<AsyncResult<Message<String>>> {
    return Handler {
        if (it.failed()) {
            throw IllegalStateException()
        } else {
            logger.info("received a message with body ${it.result().body()}")
            val coffeeMessage = coffeeMessageMapper(it.result().body()) as T
            handler(coffeeMessage)
        }
    }
}
