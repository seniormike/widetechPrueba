package com.uniandes.widetech.view.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.icu.number.NumberFormatter.with
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentResolverCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uniandes.widetech.R
import com.uniandes.widetech.view.HomeActivity
import com.uniandes.widetech.view.recyclers.RecyclerViewAdapterImages
import com.uniandes.widetech.view.recyclers.RecyclerViewAdapterSignatures
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.InputStream
import java.lang.Exception

class ProfileFragment(iList: List<String>, sList: List<String>): Fragment() {

    private var layoutManagerImg: RecyclerView.LayoutManager? = null
    private var layoutManagerSig: RecyclerView.LayoutManager? = null
    private var adapterImg: RecyclerView.Adapter<RecyclerViewAdapterImages.ViewHolder>? = null
    private var adapterSig: RecyclerView.Adapter<RecyclerViewAdapterSignatures.ViewHolder>? = null

    private var imgList: List<String> = iList
    private var sigList: List<String> = sList

    private var isImage: Boolean = false
    private var isSignature: Boolean = false




    val REQUEST_IMAGE_CAPTURE = 1
    private val IMAGE_PICK_CODE = 1000;

    /**
     * Represents if the location permission was granted to the app.
     */
    private var cameraPermissionGranted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Layout Manager
        layoutManagerImg = LinearLayoutManager(context)
        layoutManagerSig = LinearLayoutManager(context)


        // Images Recycler View
        recyclerViewProfilePictures.layoutManager = layoutManagerImg
        adapterImg =
            RecyclerViewAdapterImages(
                imgList
            )
        recyclerViewProfilePictures.adapter = adapterImg

        // Signatures Recycler View

        recyclerViewProfileSignature.layoutManager = layoutManagerSig
        adapterSig =
            RecyclerViewAdapterSignatures(
                sigList
            )
        recyclerViewProfileSignature.adapter = adapterSig

        // Get camera permission
        getCameraPermission()

        addPhoto.setOnClickListener {
            isImage = true

            val builder = AlertDialog.Builder(activity as HomeActivity)

            builder.setTitle("Cargar imagen")
            builder.setMessage("Selecciona de qué manera quieres cargar la imagen:")

            builder.setPositiveButton("Cámara") { dialog, which ->
                dispatchTakePictureIntent()
            }
            builder.setNegativeButton("Galería"){dialog,which ->
                pickImageFromGallery()
            }
            builder.show()


        }
        addSignature.setOnClickListener {
            isSignature = true

            val builder = AlertDialog.Builder(activity as HomeActivity)

            builder.setTitle("Cargar firma")
            builder.setMessage("Selecciona de qué manera quieres cargar la firma:")

            builder.setPositiveButton("Cámara") { dialog, which ->
                dispatchTakePictureIntent()
            }
            builder.setNegativeButton("Galería"){dialog,which ->
                pickImageFromGallery()
            }
            builder.show()
        }
    }
    /**
     * Method that asks for location permission.
     */
    private fun getCameraPermission() {
        cameraPermissionGranted = ContextCompat.checkSelfPermission(activity as HomeActivity,
            Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

        if (!cameraPermissionGranted) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity as HomeActivity,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(activity as HomeActivity,
                    arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                ActivityCompat.requestPermissions(activity as HomeActivity,
                    arrayOf(Manifest.permission.CAMERA), 1)
            }
        }
    }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        (activity as HomeActivity).runOnUiThread {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                val imageBitmap = data?.extras?.get("data") as Bitmap
                if (isImage) {
                    (activity as HomeActivity).saveImage(imageBitmap)
                    isImage = false
                } else if (isSignature) {
                    (activity as HomeActivity).saveSignature(imageBitmap)
                    isSignature = false
                }

            } else if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
                //val imageBitmap = Picasso.get().load(data?.data).get()
                Picasso.get().load(data?.data).into(object : com.squareup.picasso.Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        if (isImage && bitmap != null){
                            (activity as HomeActivity).saveImage(bitmap)
                            isImage = false
                        } else if (isSignature && bitmap != null){
                            (activity as HomeActivity).saveSignature(bitmap)
                            isSignature = false
                        }
                    }
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    }

                })

            }
        }

    }

}