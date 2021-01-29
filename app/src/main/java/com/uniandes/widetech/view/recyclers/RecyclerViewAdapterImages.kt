package com.uniandes.widetech.view.recyclers

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.widetech.R

class RecyclerViewAdapterImages(list: List<String>): RecyclerView.Adapter<RecyclerViewAdapterImages.ViewHolder>() {

    private var itemsList: List<String> = list

    inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        var imagen: ImageView
        var nombre: TextView
        var descripcion: TextView

        init {
            imagen = itemView.findViewById<ImageView>(R.id.imageTaken)
            nombre = itemView.findViewById<TextView>(R.id.itemImageTitle)
            descripcion = itemView.findViewById<TextView>(R.id.itemImageDescription)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_model_images,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, p: Int) {
        holder.nombre.text = "Título imagen"
        holder.descripcion.text = "Descripción imagen"

        var imageBytes = Base64.decode(itemsList[p], Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.imagen.setImageBitmap(decodedImage)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}