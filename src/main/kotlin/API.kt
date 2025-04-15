package com.example

import com.example.entrega.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.io.File

val Application.CAMINHODATA: String
    get() = "./src/main/resources/data"

var entregas = mutableListOf<Entrega>()

fun Application.startServer() {
    val pessoas = Json.decodeFromString<List<Pessoa>>(
        File("$CAMINHODATA/pessoas.json").readText()
    )
    pessoas.forEach { Pessoa.registar(it) }

    val entregasSerializadas = Json.decodeFromString<List<EntregaSerializada>>(
        File("$CAMINHODATA/entregas.json").readText()
    )
    val entregasTemp = entregasSerializadas.map { it.reconstruir()!! }
    entregas = entregasTemp as MutableList<Entrega>
}

fun Application.configureAPI(){
    // Criando dados
    // val tiago = Pessoa("p1", "Tiago")
    // val davi = Pessoa("p2", "Davi")
    // val izabella = Pessoa("p3", "Izabella")
    // listOf(tiago, davi, izabella).forEach { Pessoa.registar(it) }

    routing {
        get ("/pessoas") {
            call.respond(Pessoa.todas())
        }
        get("/entregas") {
            call.respond(entregas.map { EntregaSerializada.construir(it) })
        }
        get("/entregas-nao-entregues") {
            call.respond(entregas.filter { !it.entregue }.map { EntregaSerializada.construir(it) })
        }
        post("/cadastrarEntrega") {
            val formData = call.receiveParameters()
            if ((formData["remetente"] != null) and (formData["destinatario"] != null)){
                val rementente = Pessoa.porId(formData["remetente"]!!)
                val destinatario = Pessoa.porId(formData["destinatario"]!!)

                val jsonEntregas = Json.encodeToString(entregas.map { EntregaSerializada.construir(it) } )
                File("$CAMINHODATA/entregas.json").writeText(jsonEntregas)

                entregas.add(Entrega(rementente!!, destinatario!!, false))
                call.respondRedirect("/entregas.html")
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
            //call.respondText(formData.entries().toList().toString())


        }
    }
}