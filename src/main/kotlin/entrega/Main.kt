package com.example.entrega

import kotlinx.serialization.json.Json
import java.io.File


fun main() {

    val CAMINHODATA = "./src/main/resources/data"

    Pessoa.limpar()

    // Criando dados
    val tiago = Pessoa("p1", "Tiago")
    val davi = Pessoa("p2", "Davi")
    val izabella = Pessoa("p3", "Izabella")
    listOf(tiago, davi, izabella).forEach { Pessoa.registar(it) }

    var entregas = listOf(
        Entrega(tiago, davi, true),
        Entrega(tiago, izabella, false)
    )

    // Serializando em disco
    val jsonPessoas = Json.encodeToString(Pessoa.todas())
    File("$CAMINHODATA/pessoas.json").writeText(jsonPessoas)
    println("JSON salvo:\n$jsonPessoas")

    val jsonEntregas = Json.encodeToString(entregas.map { EntregaSerializada.construir(it) } )
    File("$CAMINHODATA/entregas.json").writeText(jsonEntregas)
    println("JSON salvo:\n$jsonEntregas")

    // Desserializando do disco
    Pessoa.limpar()
    val textPessoas = File("$CAMINHODATA/pessoas.json").readText()
    val pessoas = Json.decodeFromString<List<Pessoa>>(textPessoas)
    pessoas.forEach { Pessoa.registar(it) }
    println("Pessoas deserializados: ${Pessoa.todas()}")

    val textEntregas = File("$CAMINHODATA/entregas.json").readText()
    val entregasSerializadas = Json.decodeFromString<List<EntregaSerializada>>(textEntregas)
    entregas = entregasSerializadas.map { it.reconstruir()!! }
    println("Entregas deserializados: ${entregas}")
}