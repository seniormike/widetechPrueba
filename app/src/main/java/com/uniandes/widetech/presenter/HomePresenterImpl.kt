
package com.uniandes.widetech.presenter

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import com.google.firebase.Timestamp
import com.uniandes.widetech.db.DbHelper
import com.uniandes.widetech.model.ProductVO
import com.uniandes.widetech.view.HomeView
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Class that represents MainPresenterImpl
 * @author Miguel Angel Puentes
 */
class HomePresenterImpl(private val homeView: HomeView): HomePresenter{

    /**
     * Client to make http requests
     */
    private val client = OkHttpClient()

    /**
     * List of products
     */
    private var productsList: MutableList<ProductVO> = ArrayList()

    /**
     * List of images
     */
    private var imagesList: MutableList<String> = ArrayList()

    /**
     * List of images
     */
    private var signaturesList: MutableList<String> = ArrayList()


    /**
     * SQLite Database access Helper reference.
     */
    private lateinit var dbHelper: DbHelper

    /**
     * SQLite Database reference for writing operations.
     */
    private lateinit var dbLiteWritable: SQLiteDatabase

    /**
     * SQLite Database reference for writing operations.
     */
    private lateinit var dbLiteReadable: SQLiteDatabase


    /**
     * @see HomePresenter.getProducts
     */
    override fun getProducts(){
        val requestBody = RequestBody.create(JSON, "")
        val request = Request.Builder()
            .method("POST",requestBody )
            .url("http://ws4.shareservice.co/TestMobile/rest/GetProductsData")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("<REQUEST FAIL>",e.message.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                Log.e("SUCCESS","SUCCESS ADDED EMERGENCY")
                var result: String = response.body()!!.string()
                Log.e("JSONNU",result)
                var jsonArray = JSONArray(result)
                saveProductsList(jsonArray)
                homeView.loadProductsList()
            }
        })
    }

    /**
     * Method that saves an emergency in cache.
     */
    fun saveImageInDB(image: String, type: Int){
        val values = ContentValues().apply {
            put(DbHelper.FeedReaderContract.FeedEntry.COLUMN_IMAGE, image)
            put(DbHelper.FeedReaderContract.FeedEntry.COLUMN_TYPE, type)
        }
        // Insert the new row, returning the primary key value of the new row
        val newRowId = dbLiteWritable?.insert(DbHelper.FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
    }

    fun getImagesInDB(t: Int): List<String>{

        val cursor = dbLiteReadable.query(
            DbHelper.FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null               // The sort order
        )


        if (t == 0){
            imagesList = ArrayList()
        } else if(t == 1)
        {
            signaturesList = ArrayList()
        }

        with(cursor) {
            while (moveToNext()) {
                var image: String = cursor.getString(cursor.getColumnIndex(DbHelper.FeedReaderContract.FeedEntry.COLUMN_IMAGE))
                var type: Int = cursor.getInt(cursor.getColumnIndex(DbHelper.FeedReaderContract.FeedEntry.COLUMN_TYPE))
                if (t == 0 && t == type)
                {
                    imagesList.add(image)
                } else if (t == 1 && t == type){
                    signaturesList.add(image)
                }
            }
        }
        return if (t == 0){
            imagesList
        }else {
            signaturesList
        }
    }


    /**
     * Function that initializes the database
     */
    fun initDB(){
        dbHelper = DbHelper(homeView.getAppContext()!!)
        dbLiteWritable = dbHelper.writableDatabase
        dbLiteReadable = dbHelper.readableDatabase
    }

    override fun getProductsList():List<ProductVO>{
        return productsList
    }

    override fun saveProductsList(jsonArray: JSONArray){
        for (i in 0 until jsonArray.length()){
            var actual = jsonArray.getJSONObject(i)
            val nombre = actual.optString("Name")
            val descripcion = actual.optString("Description")
            val url = actual.optString("ImageUrl")
            var productoActual = ProductVO(nombre, descripcion, url)
            Log.e("ProductoActual",productoActual.toString())
            productsList.add(productoActual)
        }
    }

    override fun getImagesList(): List<String>{
        return imagesList
    }

    override fun getSignaturesList(): List<String>{
        return signaturesList
    }
    override fun addImage(image: String){
        //imagesList.add(image)
        saveImageInDB(image,0)
        homeView.loadImageSignatureList()
    }

    override fun addSignature(image: String){
        //signaturesList.add(image)
        saveImageInDB(image,1)
        homeView.loadImageSignatureList()
    }

    companion object {
        val JSON = MediaType.parse("application/json; charset=utf-8")
    }
}
