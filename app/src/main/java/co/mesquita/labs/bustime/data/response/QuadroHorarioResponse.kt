package co.mesquita.labs.bustime.data.response

data class HorarioResponse(
    val horario: String,
    val dsdiatipo: String,
    val dssublinha: String,
    val etiqueta: String
)

data class QuadroHorarioData(
    val ida: List<HorarioResponse>,
    val volta: List<HorarioResponse>
)

// Resposta de GET rumo/json-quadro-horario/
data class QuadroHorarioResponse(
    val quadro_horario: QuadroHorarioData
)
