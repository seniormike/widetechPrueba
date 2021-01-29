package com.uniandes.widetech.view.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uniandes.widetech.R
import com.uniandes.widetech.model.ProductVO

class RecyclerViewAdapter(list: List<ProductVO>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var itemsList: List<ProductVO> = list

    inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        var imagen: ImageView
        var nombre: TextView
        var descripcion: TextView

        init {
            imagen = itemView.findViewById<ImageView>(R.id.itemImage)
            nombre = itemView.findViewById<TextView>(R.id.itemTitle)
            descripcion = itemView.findViewById<TextView>(R.id.itemDescription)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_model,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, p: Int) {
        holder.nombre.text = itemsList[p].nombre
        holder.descripcion.text = itemsList[p].descripcion
        Picasso.get().load(itemsList[p].url).into(holder.imagen)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}