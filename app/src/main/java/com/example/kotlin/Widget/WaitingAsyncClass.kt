package com.example.kotlin.Widget

import android.os.AsyncTask
import retrofit2.Call

public class WaitingAsyncClass<T>(var call: Call<T>): AsyncTask<String, Void, T>(){
    override fun doInBackground(vararg p0: String?): T? {
        var respone = call.execute()
        if(respone.isSuccessful){
            return respone.body()!!
        }else
            return null

    }
    override fun onPostExecute(result: T?) {
        super.onPostExecute(result)
    }
}