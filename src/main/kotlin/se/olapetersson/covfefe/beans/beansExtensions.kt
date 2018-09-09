package se.olapetersson.covfefe.beans

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json
import se.olapetersson.covfefe.beans.BeansRepository.Companion.ADDRESS

fun EventBus.addCoffeeBean(addBeanRequest: AddBeanRequest, handler: (AddBeanResponse) -> Unit) {
    this.send<String>(
        BeansRepository.ADDRESS,
        Json.encode(addBeanRequest),
        se.olapetersson.covfefe.beans.unwrapPayload(handler))
}

fun EventBus.consumeCoffeBeanRequest(handler: (AddBeanRequest, Message<String>) -> Unit) {
    this.consumer<String>(ADDRESS) { message ->
        val serializedRequest = Json.decodeValue(message.body(), AddBeanRequest::class.java)
        val addBeanResponse = handler(serializedRequest, message)
    }
}

private fun <T> unwrapPayload(handler: (T) -> Unit): Handler<AsyncResult<Message<String>>> {
    return Handler {
        if (it.failed()) {
            throw IllegalStateException()
        } else {
            val serializedObject = Json.decodeValue(
                it.result().body(), Any::class.java) as T
            handler(serializedObject)
        }
    }
}
