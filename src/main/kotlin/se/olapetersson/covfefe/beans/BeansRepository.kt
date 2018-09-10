package se.olapetersson.covfefe.beans

import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

class BeansRepository : AbstractVerticle() {

    val tableName = "beans"

    //TODO: make this have the same address but with some msg-routing
    companion object {
        val ADDRESS = "beans.repository"
        val ADDRESS_READ = "beans.repository.read"
    }

    val logger = LoggerFactory.getLogger(BeansRepository::class.java.name)

    val eventBus: EventBus by lazy { vertx.eventBus() }
    lateinit var client: MongoClient

    override fun start() {
        // Vertx.currentContext().config()
        // setupCreateBook()
        // setupFindAll()
        client = setupMongoClient()
        eventBus.consumer<String>(ADDRESS) { message ->
            val payload = coffeeMessageMapper(message.body())
            when(payload) {
                is AddBeanRequest -> addBean(payload, message)
                is GetAllBeansRequest -> getAllBeans(message)
            }

        }
    }

    private fun setupMongoClient(): MongoClient {
        val mongoUrl = "mongodb://localhost:27017"
        val mongoPassword = "password"
        val dbName = "test"
        // This is supposedly how you can set applicatio config later on
        val mongoConfig = json {
            obj(
                "connection_string" to mongoUrl,
                "db_name" to dbName
            )
        }
        return MongoClient.createNonShared(vertx, mongoConfig)
    }

    fun addBean(addBeanRequest: AddBeanRequest, message: Message<String>) {
        logger.info("I have received a message: $addBeanRequest")

        val beanDto = json {
            obj(
                "_id" to addBeanRequest.name,
                "name" to addBeanRequest.name
            )
        }

        client.save(tableName, beanDto) { res ->
            if (res.succeeded()) {
                logger.info("Successfully saved a bean")
                val jsonString = Json.encode(AddBeanResponse(addBeanRequest.name))
                logger.info("Continuing with $jsonString")
                message.reply(jsonString)
            } else {
                message.fail(1, "Unable to save")
                throw IllegalStateException("unable to save")
            }
        }
    }

    fun getAllBeans(message: Message<String>) {
        logger.info("I received a message to find all${message.body()}")
        var query = json {
            obj()
        }
        client.find(tableName, query) { res ->
            if (res.succeeded()) {
                logger.info("Received all the beans")
                message.reply(res.result().toString())
            } else {
                logger.error("Failed to get all beans ${res.cause().printStackTrace()}")
                message.fail(2, "FAILED IN READ")
            }
        }
    }

    fun setupFindAll() {
        eventBus.consumer<Any>(ADDRESS_READ) { message ->
            logger.info("I received a message to find all${message.body()}")
            var query = json {
                obj()
            }
            client.find(tableName, query) { res ->
                if (res.succeeded()) {
                    logger.info("Received all the beans")
                    message.reply(res.result().toString())
                } else {
                    logger.error("Failed to get all beans ${res.cause().printStackTrace()}")
                    message.fail(2, "FAILED IN READ")
                }
            }

        }
    }

    fun setupCreateBook() {
        // eventBus.consumeAddBeanResponse {
        // }

        eventBus.consumeCoffeeBeanRequest { beanRequest, message ->



        }
    }

    override fun stop() {
        logger.info("Stopping ${this::class}")
    }
}

