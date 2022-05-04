package com.goldmedal.hrapp.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.goldmedal.hrapp.util.NoInternetException
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.URL
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection


class NetworkConnectionInterceptor @Inject constructor(
        @ApplicationContext context: Context
) : Interceptor { //,private val networkConnectivity: NetworkConnectivity

    private val applicationContext = context.applicationContext
    override fun intercept(chain: Interceptor.Chain): Response {
        if(!checkActiveInternet())
            throw NoInternetException("Make sure you have an active data connection")
//        val request: Request = chain.request().newBuilder().addHeader("parameter", "value").build()
        return chain.proceed(chain.request())
    }

    private  fun checkActiveInternet() : Boolean{
            if (isInternetAvailable()) {
                var connection: HttpsURLConnection? = null
                try {
                    //efficiently ping Google site to check device has an active internet connection
                    connection = URL("https://clients3.google.com/generate_204").openConnection() as HttpsURLConnection
                    connection.setRequestProperty("User-Agent", "Android")
                    connection.setRequestProperty("Connection", "close")
                    connection.connectTimeout = 1000
                    connection.connect()
                    val isConnected = connection.responseCode == 204 && connection.contentLength == 0
                    connection.disconnect()
                    return isConnected
                } catch (e: Exception) {
                    connection?.disconnect()
                    //No Connection
                    return false
                }
        }else{
                //No Connection
                return false
        }
    }

    private   fun isInternetAvailable() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var result = false
            val connectivityManager =
                    applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            connectivityManager?.let {

                it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        else -> false
                    }
                }
            }
            return result
        }
        else{
            val connectivityManager =
                    applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.activeNetworkInfo.also {
                return it != null && it.isConnected
            }
        }
    }
    }
