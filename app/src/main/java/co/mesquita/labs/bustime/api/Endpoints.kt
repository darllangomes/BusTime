package co.mesquita.labs.bustime.api

import co.mesquita.labs.bustime.data.response.CalendarioOperacionalResponse
import co.mesquita.labs.bustime.data.response.QuadroHorarioResponse
import co.mesquita.labs.bustime.data.response.StopLineResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Endpoints {
    // Linhas que atendem uma parada. Lista vazia = parada inexistente.
    @GET("rumo/json_modal_paradas/")
    suspend fun getStopLines(
        @Query("codigo-parada") stopId: String
    ): Response<List<StopLineResponse>>

    // Página HTML da linha, usada apenas para extrair (via Jsoup) a vigência
    // (data de início/fim) do quadro de horário vigente de cada tipo de dia.
    @GET("rumo/")
    suspend fun getLinePage(
        @Query("codigo-linha") lineCode: String
    ): Response<String>

    // Indica se a data consultada é uma exceção no calendário (feriado etc.)
    @GET("rumo/json_calendario_operacional/")
    suspend fun getOperatingCalendar(
        @Query("data") date: String
    ): Response<CalendarioOperacionalResponse>

    // Quadro de horários (ida/volta) de uma linha para um tipo de dia e vigência.
    @GET("rumo/json-quadro-horario/")
    suspend fun getSchedule(
        @Query("diatipo") dayType: String,
        @Query("codigolinha") lineCode: String,
        @Query("dtiniciovalidade") validFrom: String,
        @Query("dtterminovalidade") validUntil: String
    ): Response<QuadroHorarioResponse>
}
