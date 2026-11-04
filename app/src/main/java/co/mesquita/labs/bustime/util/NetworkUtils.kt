package co.mesquita.labs.bustime.util

import co.mesquita.labs.bustime.util.Constants.BASE_RUMO_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class NetworkUtils {
    companion object {
        fun getJsonInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_RUMO_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getHtmlInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_RUMO_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        }
    }
}
