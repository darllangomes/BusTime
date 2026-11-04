package co.mesquita.labs.bustime.data.response

// Resposta de GET rumo/json_modal_paradas/?codigo-parada={stopId}
data class StopLineResponse(
    val codigoLinha: String,
    val nomeLinha: String,
    val nome: String,
    val classe: String,
    val referencia: String?,
    val nodo: Int,
    val codigoSublinha: Int
)
