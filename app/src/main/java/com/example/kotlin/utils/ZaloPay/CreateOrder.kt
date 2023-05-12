package com.example.kotlin.utils.ZaloPay

import android.util.Log
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.TlsVersion
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Date
import java.util.concurrent.TimeUnit

object AppInfo {
    const val APP_ID = 2553
    const val MAC_KEY = "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL"
    const val URL_CREATE_ORDER = "https://sb-openapi.zalopay.vn/v2/create"
}
class CreateOrder {
    inner class CreateOrderData  constructor(amount: String) {
        var AppId: String
        var AppUser: String
        var AppTime: String
        var Amount: String
        var AppTransId: String
        var EmbedData: String
        var Items: String
        var BankCode: String
        var Description: String
        var Mac: String

        init {
            val appTime: Long = Date().time
            AppId = AppInfo.APP_ID.toString()
            AppUser = "Android_Demo"
            AppTime = appTime.toString()
            Amount = amount
            AppTransId = Helpers.appTransId
            EmbedData = "{}"
            Items = "[]"
            BankCode = "zalopayapp"
            Description = "Merchant pay for order #" + Helpers.appTransId
            val inputHMac = String.format(
                "%s|%s|%s|%s|%s|%s|%s",
                AppId,
                AppTransId,
                AppUser,
                Amount,
                AppTime,
                EmbedData,
                Items
            )
            Mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac)
        }
    }

    @Throws(Exception::class)
    fun createOrder(amount: String): JSONObject {
        val input = CreateOrderData(amount)
        val formBody: okhttp3.RequestBody = FormBody.Builder()
            .add("app_id", input.AppId)
            .add("app_user", input.AppUser)
            .add("app_time", input.AppTime)
            .add("amount", input.Amount)
            .add("app_trans_id", input.AppTransId)
            .add("embed_data", input.EmbedData)
            .add("item", input.Items)
            .add("bank_code", input.BankCode)
            .add("description", input.Description)
            .add("mac", input.Mac)
            .build()
        return HttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody)!!
    }
}
class HttpProvider {
    companion object {
        fun sendPost(URL: String?, formBody: okhttp3.RequestBody?): JSONObject? {
            var data: JSONObject? = JSONObject()
            try {
                val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
                    )
                    .build()
                val client: OkHttpClient = OkHttpClient.Builder()
                    .connectionSpecs(listOf<ConnectionSpec>(spec))
                    .callTimeout(5000, TimeUnit.MILLISECONDS)
                    .build()
                val request: Request = Request.Builder()
                    .url(URL!!)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(formBody!!)
                    .build()
                val response = client.newCall(request).execute()
                data = if (!response.isSuccessful) {
                    Log.println(Log.ERROR, "BAD_REQUEST", response.body!!.string())
                    null
                } else {
                    JSONObject(response.body!!.string())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return data
        }
    }
}