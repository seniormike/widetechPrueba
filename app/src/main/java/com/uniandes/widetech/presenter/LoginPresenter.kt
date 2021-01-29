package com.uniandes.widetech.presenter


/**
 * Class that represents TorchPresenter
 * @author Miguel Angel Puentes
 */
interface LoginPresenter {

    /**
     * Method that makes login request to http service.
     */
    fun login(username: String, password: String)
}