package com.uniandes.widetech.presenter

import com.uniandes.widetech.model.ProductVO
import org.json.JSONArray

/**
 * Class that represents HomePresenter
 * @author Miguel Angel Puentes
 */
interface HomePresenter {
    fun getProducts()
    fun saveProductsList(jsonArray: JSONArray)
    fun getProductsList():List<ProductVO>
    fun getSignaturesList():List<String>
    fun getImagesList(): List<String>
    fun addImage(image: String)
    fun addSignature(signature: String)

}