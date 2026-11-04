package co.mesquita.labs.bustime.data.response

data class CalendarioDia(
    val excecao: Boolean,
    val dsdiatipo: String? = null
)

// Resposta de GET rumo/json_calendario_operacional/?data=yyyy-MM-dd
data class CalendarioOperacionalResponse(
    val dados: List<CalendarioDia>
)
