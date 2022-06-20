package app.ibiocd.odontologia

import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

object RetrofitClient {

    val instance:APIService by lazy {
        val retrofit=Retrofit.Builder()
            .baseUrl("http://23herrera.xyz:81/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()

        retrofit.create(APIService::class.java)
    }

    private fun getClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .build()

}