package se.olapetersson.covfefe

import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
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
        // This is supposedly how you can set applicatio config later on
        // Vertx.currentContext().config()
        val mongoUrl = "mongodb://localhost:27017"
        val mongoPassword = "password"
        val dbName = "test"

        val mongoConfig = json {
            obj(
                "connection_string" to mongoUrl,
                "db_name" to dbName
            )
        }
        client = MongoClient.createNonShared(vertx, mongoConfig)
        setupCreateBook()
        setupFindAll()
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
        eventBus.consumer<JsonObject>(ADDRESS) { message ->
            logger.info("I have received a message: ${message.body()}")
            val beanDto = json {
                obj(
                    "_id" to message.body().map["name"],
                    "name" to message.body().map["name"]
                )
            }

            client.save(tableName, beanDto) { res ->
                if (res.succeeded()) {
                    logger.info("Successfully saved a bean")
                    message.reply(res.result())
                } else {
                    message.fail(1, "FAILED IN WRITE")
                }
            }


        }
    }

    override fun stop() {
        logger.info("Stopping ${this::class}")
    }
}

