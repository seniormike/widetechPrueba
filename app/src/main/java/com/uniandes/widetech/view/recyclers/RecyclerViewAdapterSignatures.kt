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

class RecyclerViewAdapterSignatures(list: List<String>): RecyclerView.Adapter<RecyclerViewAdapterSignatures.ViewHolder>() {

    private var itemsList: List<String> = list

    inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        var imagen: ImageView
        var nombre: TextView
        var descripcion: TextView

        init {
            imagen = itemView.findViewById<ImageView>(R.id.signatureTaken)
            nombre = itemView.findViewById<TextView>(R.id.itemSignatureTitle)
            descripcion = itemView.findViewById<TextView>(R.id.itemSignatureDescription)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_model_signatures,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, p: Int) {
        holder.nombre.text = "Título de la firma"
        holder.descripcion.text = "Descripción de la firma"
        var imageBytes = Base64.decode(itemsList[p], Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.imagen.setImageBitmap(decodedImage)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}