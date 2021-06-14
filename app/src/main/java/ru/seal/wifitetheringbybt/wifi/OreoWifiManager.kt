package com.example.mybtapp.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.dx.stock.ProxyBuilder

@RequiresApi(Build.VERSION_CODES.O)
class OreoWifiManager(private val context: Context) : HotspotManager() {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * This enables tethering using the ssid/password defined in Settings App>Hotspot & tethering
     * Does not require app to have system/privileged access
     * Credit: Vishal Sharma - https://stackoverflow.com/a/52219887
     */
    override fun start() {
        val outputDir = context.codeCacheDir
        val proxy: Any?

        try {
            proxy = ProxyBuilder.forClass(onStartTetheringCallbackClass())
                .dexCache(outputDir).handler { createdProxy, method, args ->
                    when (method.name) {
                        "onTetheringStarted" -> hotspotLiveData.postValue(HotspotEvent(HotspotEvent.Type.START_SUCCESS).apply {
                            ssid = "Test"
                            password = "12345678"
                        })
                        "onTetheringFailed" -> hotspotLiveData.postValue(HotspotEvent(HotspotEvent.Type.START_ERROR))
                        else -> ProxyBuilder.callSuper(createdProxy, method, *args)
                    }
                    null
                }.build()

            try {
                val method = connectivityManager.javaClass.getDeclaredMethod(
                    "startTethering",
                    Int::class.javaPrimitiveType,
                    Boolean::class.javaPrimitiveType,
                    onStartTetheringCallbackClass(),
                    Handler::class.java
                )
                method.invoke(
                    connectivityManager,
                    ConnectivityManager.TYPE_MOBILE,
                    false,
                    proxy,
                    null
                )
                Log.d( "My_WiFi","startTethering invoked")
            } catch (e: Exception) {
                Log.e( "My_WiFi","Error in enableTethering")
                e.printStackTrace()
                hotspotLiveData.postValue(
                    HotspotEvent(
                        HotspotEvent.Type.START_ERROR,
                        "Error in enableTethering"
                    )
                )
            }
        } catch (e: Exception) {
            Log.e( "My_WiFi","Error in enableTethering ProxyBuilder")
            e.printStackTrace()
            hotspotLiveData.postValue(
                HotspotEvent(
                    HotspotEvent.Type.START_ERROR,
                    "Error in enableTethering ProxyBuilder"
                )
            )
        }
    }

    override fun stop() {
        try {
            val method = connectivityManager.javaClass.getDeclaredMethod(
                "stopTethering",
                Int::class.javaPrimitiveType
            )
            method.invoke(connectivityManager, ConnectivityManager.TYPE_MOBILE)
            Log.d( "My_WiFi","stopTethering invoked")
            hotspotLiveData.postValue(HotspotEvent(HotspotEvent.Type.STOP_SUCCESS))
        } catch (e: Exception) {
            Log.e( "My_WiFi","stopTethering error: $e")
            e.printStackTrace()
            hotspotLiveData.postValue(
                HotspotEvent(
                    HotspotEvent.Type.STOP_ERROR,
                    "Error ${e.message}"
                )
            )
        }
    }

    @SuppressLint("PrivateApi")
    private fun onStartTetheringCallbackClass(): Class<*>? {
        try {
            return Class.forName("android.net.ConnectivityManager\$OnStartTetheringCallback")
        } catch (e: ClassNotFoundException) {
            Log.e( "My_WiFi","onStartTetheringCallbackClass error: %s", e)
            e.printStackTrace()
        }

        return null
    }
}
