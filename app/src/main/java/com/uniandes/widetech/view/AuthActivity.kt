package com.uniandes.widetech.view


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.uniandes.widetech.R
import com.uniandes.widetech.presenter.LoginPresenterImpl
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity(), AuthView {


    /**
     * Main presenter reference.
     */
    private var presenter: LoginPresenterImpl? = null

    /**
     * @see AppCompatActivity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_auth)


        // Presenter initialization
        presenter = LoginPresenterImpl(this)

        // Setup
        setup()
    }

    /**
     * @see AuthView.setup
     */
    override fun setup() {
        title = "Autenticación"
        loginButton.setOnClickListener {
            if(isConnected()) {
                if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                    showProgressBar()
                    presenter!!.login(emailEditText.text.toString(), passwordEditText.text.toString())
                }
                else {
                    val toast = Toast.makeText(applicationContext, getString(R.string.noFields), Toast.LENGTH_LONG)
                    toast.show()
                }
            } else{
                val toast = Toast.makeText(applicationContext, getString(R.string.noInternet), Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun showHome() {
        val homeActivity = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", "example")
        }
        startActivity(homeActivity)
    }

    /**
     * Method that shows the progress bar.
     */
    override fun showProgressBar(){
        runOnUiThread {
            progress_bar_login.visibility = View.VISIBLE
        }
    }

    /**
     * Method that hides the progress bar.
     */
    override fun hideProgressBar(){
        runOnUiThread {
            progress_bar_login.visibility = View.GONE
        }
    }

    private fun checkEmailPwd(mail: String, password: String): Boolean {
        var exito = true;
        if(!mail.endsWith("@uniandes.edu.co"))
        {
            showAlert("Debes registrarte con correo uniandes");
            exito = false;
        }

        if(password.length < 8)
        {
            showAlert("La contraseña debe tener más de 8 caracteres");
            exito = false;
        }
        return exito;
    }

    /**
     * Method that indicates if there is any internet connection.
     */
    override fun isConnected(): Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun getAppContext(): Context? {
        return applicationContext
    }
}