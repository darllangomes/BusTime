package co.mesquita.labs.bustime.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mesquita.labs.bustime.api.Endpoints
import co.mesquita.labs.bustime.data.ArrivalTime
import co.mesquita.labs.bustime.data.Bus
import co.mesquita.labs.bustime.data.response.StopLineResponse
import co.mesquita.labs.bustime.util.NetworkUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class BusViewModel : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isValidStopId = MutableLiveData<Boolean?>(null)
    val isValidStopId: LiveData<Boolean?> = _isValidStopId

    private val _busList = MutableLiveData<List<Bus>>()
    val busList: LiveData<List<Bus>> = _busList

    private val jsonService by lazy { NetworkUtils.getJsonInstance().create(Endpoints::class.java) }
    private val htmlService by lazy { NetworkUtils.getHtmlInstance().create(Endpoints::class.java) }

    fun resetValidation() {
        _isValidStopId.value = null
        _isLoading.value = false
    }

    fun resetBusList() {
        _busList.value = emptyList()
    }

    fun validateStopId(stopId: Int) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = jsonService.getStopLines(stopId.toString())
                _isValidStopId.postValue(response.isSuccessful && !response.body().isNullOrEmpty())
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("Rumo", "Exception ${e.message}")
                _isValidStopId.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateBusTable(stopId: Int) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lines = jsonService.getStopLines(stopId.toString()).body().orEmpty()
                val dayType = resolveDayType(LocalDate.now())
                val now = LocalTime.now()

                val buses = lines
                    .distinctBy { it.codigoLinha }
                    .map { line -> buildBusEntry(line, dayType, now) }
                _busList.postValue(buses)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("Rumo", "Exception ${e.message}")
            }
            _isLoading.postValue(false)
        }
    }

    // O Rumo não expõe previsão de chegada ao vivo publicamente: o que temos
    // é o quadro de horários oficial da linha. "Próximo"/"Seguinte" aqui são
    // os dois horários programados mais próximos a partir de agora.
    private suspend fun buildBusEntry(line: StopLineResponse, dayType: String, now: LocalTime): Bus {
        val upcoming = fetchUpcomingTimes(line.codigoLinha, dayType, now)
        val next = upcoming.getOrNull(0)
        val following = upcoming.getOrNull(1)

        return Bus(
            number = line.codigoLinha,
            destination = line.nomeLinha,
            next = next?.let { ArrivalTime(time = formatTime(it), isReal = true) }
                ?: ArrivalTime(time = "--", isReal = false),
            following = following?.let { ArrivalTime(time = formatTime(it), isReal = true) }
                ?: ArrivalTime(time = "--", isReal = false)
        )
    }

    private suspend fun fetchUpcomingTimes(lineCode: String, dayType: String, now: LocalTime): List<LocalTime> {
        val window = fetchValidityWindow(lineCode, dayType) ?: return emptyList()
        return try {
            val schedule = jsonService
                .getSchedule(dayType, lineCode, window.first, window.second)
                .body()
                ?.quadro_horario ?: return emptyList()

            (schedule.ida + schedule.volta)
                .mapNotNull { parseTime(it.horario) }
                .filter { it >= now }
                .sorted()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e("Rumo", "Exception ${e.message}")
            emptyList()
        }
    }

    // A vigência (data de início/fim) do quadro de horário atual não vem em
    // nenhum endpoint JSON: ela é renderizada na própria página HTML da linha,
    // então extraímos com Jsoup, como o próprio site do Rumo faz no navegador.
    private suspend fun fetchValidityWindow(lineCode: String, dayType: String): Pair<String, String>? {
        val html = htmlService.getLinePage(lineCode).body() ?: return null
        val option = Jsoup.parse(html)
            .select("#dia-semana-select option")
            .firstOrNull { it.attr("data-dsdiatipo") == dayType }
            ?: return null

        val validFrom = option.attr("data-dtinicio")
        val validUntil = option.attr("data-dttermino")
        if (validFrom.isBlank() || validUntil.isBlank()) return null

        return validFrom to validUntil
    }

    private suspend fun resolveDayType(date: LocalDate): String {
        val defaultDayType = when (date.dayOfWeek) {
            DayOfWeek.SATURDAY -> "SAB"
            DayOfWeek.SUNDAY -> "DOM"
            else -> "DUT"
        }
        return try {
            val exception = jsonService.getOperatingCalendar(date.toString()).body()?.dados?.firstOrNull()
            if (exception?.excecao == true && !exception.dsdiatipo.isNullOrBlank()) {
                exception.dsdiatipo
            } else {
                defaultDayType
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            defaultDayType
        }
    }

    private fun parseTime(value: String): LocalTime? {
        return try {
            LocalTime.parse(value, TIME_FORMATTER)
        } catch (e: Exception) {
            null
        }
    }

    private fun formatTime(time: LocalTime): String = time.format(TIME_FORMATTER)

    private companion object {
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
