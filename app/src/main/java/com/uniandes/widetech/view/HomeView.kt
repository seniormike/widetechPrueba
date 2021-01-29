package com.uniandes.widetech.view

import android.content.Context

/**
 * Class that represents HomeView
 * @author Miguel Angel Puentes
 */
interface HomeView {

    /**
     * Method that says if the user is connected to internet.
     */
    fun isConnected(): Boolean

    /**
     * Method that loads products list.
     */
    fun loadProductsList()

    /**
     * Method that loads images list.
     */
    fun loadImageSignatureList()

    /**
     * Gets app context
     */
    fun getAppContext(): Context?


}