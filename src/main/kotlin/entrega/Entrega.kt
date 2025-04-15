package com.example.entrega

import kotlinx.serialization.Serializable

class Entrega(
    val remetente: Pessoa,
    val destinatario: Pessoa,
    val entregue: Boolean
) {
    override fun toString(): String {
        return "De: ${remetente} Para: ${destinatario} | ${entregue}"
    }
}

// versão serializável
@Serializable
data class EntregaSerializada(
    val remetenteId: String,
    val destinatarioId: String,
    val entregue: Boolean
) {
    fun reconstruir(): Entrega? {
        val remetente = Pessoa.porId(remetenteId)
        val destinatario = Pessoa.porId(destinatarioId)
        if (remetente != null && destinatario != null) {
            return Entrega(remetente, destinatario, entregue)
        }
        return null
    }

    companion object {
        fun construir(entrega: Entrega): EntregaSerializada =
            EntregaSerializada(
                entrega.remetente.id,
                entrega.destinatario.id,
                entrega.entregue
            )
    }
}