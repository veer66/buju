package rocks.veer66

import rocks.veer66.query.createDatasetFromTtl
import rocks.veer66.query.query
import me.grison.jtoml.impl.Toml
import java.io.File
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    println(System.getProperty("user.dir"))
    val toml = Toml.parse(File("config.toml").readText())
    val conf = toml.getMap("server")
    val port = conf.get("port") as Long
    val idxDir = conf.get("idx_dir") as String
    val ttlPath = conf.get("ttl_path") as String
    val dataset = createDatasetFromTtl(ttlPath, idxDir)
    query(dataset, """PREFIX spatial: <http://jena.apache.org/spatial#>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        SELECT ?p  WHERE {
            ?p spatial:nearby (12.6813 101.2816 1000.0 'km') .
        }""")

    val server = embeddedServer(Netty, port.toInt()) {
        routing {
            get("/") {
                call.respondText("Hello, world!", ContentType.Text.Html)
            }
        }
    }
    println("Listening ...")
    server.start(wait = true)
}

