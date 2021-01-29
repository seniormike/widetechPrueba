package com.uniandes.widetech.presenter

import android.util.Log
import com.uniandes.widetech.presenter.HomePresenterImpl.Companion.JSON
import com.uniandes.widetech.view.AuthView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

/**
 * Class that represents TorchPresenterImp
 * @author Miguel Angel Puentes
 */
class LoginPresenterImpl(private val loginView: AuthView): LoginPresenter {


    /**
     * Client to make http requests
     */
    private val client = OkHttpClient();


    /**
     * @see LoginPresenter.login
     */
    override fun login(username: String, password: String){
        val jsonString = """{"UserName": "$username", "Password" : "$password" }"""
        val requestBody = RequestBody.create(JSON, jsonString)
        val request = Request.Builder()
            .method("POST",requestBody )
            .url("http://ws4.shareservice.co/TestMobile/rest/Login")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("<REQUEST FAIL>",e.message.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                Log.e("SUCCESS","SUCCESS ADDED EMERGENCY")
                var result: String = response.body()!!.string()
                loginView.hideProgressBar()
                loginView.showHome()
            }
        })
    }


}