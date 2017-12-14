package rocks.veer66

import rocks.veer66.query.createDatasetFromTtl
import rocks.veer66.query.query
import me.grison.jtoml.impl.Toml
import java.io.File
import io.ktor.application.*
import io.ktor.content.readText
import io.ktor.http.*
import io.ktor.request.receiveText
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.apache.jena.query.ResultSetFormatter
import java.io.ByteArrayOutputStream
import java.io.StringWriter
import java.io.Writer

fun main(args: Array<String>) {
    println(System.getProperty("user.dir"))
    val toml = Toml.parse(File("config.toml").readText())
    val conf = toml.getMap("server")
    val port = conf.get("port") as Long
    val idxDir = conf.get("idx_dir") as String
    val ttlPath = conf.get("ttl_path") as String
    val dataset = createDatasetFromTtl(ttlPath, idxDir)

    val server = embeddedServer(Netty, port.toInt()) {
        routing {
            get("/") {
                call.respondText("Buju!", ContentType.Text.Html)
            }
            post("/query") {
                call.respondText(ContentType.Application.Json) {
                    val q = call.receiveText()
                    val results = query(dataset, q)
                    val buf = ByteArrayOutputStream()
                    ResultSetFormatter.outputAsJSON(buf, results);
                    String(buf.toByteArray())
                }
            }
        }
    }
    println("Listening ...")
    server.start(wait = true)
}

