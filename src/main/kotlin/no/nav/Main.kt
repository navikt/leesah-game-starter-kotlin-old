package no.nav

import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.rapid.Config
import no.nav.rapid.RapidServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

lateinit var logger: Logger

fun main() {
    val config = Config.fromEnv()
    logger = LoggerFactory.getLogger(config.appName)
    val app = QuizApplication(config.appName)
    RapidServer(config, ::ktorServer, app).startBlocking()
}

fun ktorServer(appName: String, isReady: () -> Boolean): ApplicationEngine = embeddedServer(CIO, applicationEngineEnvironment {
    connector {
        port = 8080
    }
    module {
        install(ContentNegotiation) { jackson() }

        routing {

            get("/hello") {
                call.respondText("Hello")
            }

            get("/secure") { call.respondText("Secure endpoint") }


            get("/") {
                call.respondText(
                    "<html><h1>$appName</h1><html>",
                    ContentType.Text.Html
                )
            }

            get("/alive") {
                call.respond("OK")
            }
            get("/ready") {
                if (isReady()) call.respond("OK") else call.respond(HttpStatusCode.ServiceUnavailable)
            }

        }
    }

})




