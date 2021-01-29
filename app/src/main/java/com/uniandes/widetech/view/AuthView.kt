package com.uniandes.widetech.view

/**
 * Class that represents MainView
 * @author Miguel Angel Puentes
 */
interface AuthView {

    /**
     * Method that says if the user is connected to internet.
     */
    fun isConnected(): Boolean

    /**
     * Method that initializes button listeners and visuals.
     */
    fun setup()

    /**
     * Method that initializes Home activity.
     */
    fun showHome()

    /**
     * Method that hides progress bar.
     */
    fun hideProgressBar()

    /**
     * Method that shows progress bar.
     */
    fun showProgressBar()
}