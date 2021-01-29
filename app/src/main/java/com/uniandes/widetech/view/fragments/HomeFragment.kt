package com.uniandes.widetech.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.uniandes.widetech.R
import com.uniandes.widetech.model.ProductVO
import com.uniandes.widetech.view.HomeActivity
import com.uniandes.widetech.view.recyclers.RecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_home.*

/**
 *
 */
class HomeFragment(prodList: List<ProductVO>) : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>? = null
    private var list: List<ProductVO> = prodList


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * Method that shows the progress bar.
     */
    private fun showProgressBar(){
        progress_bar_home.visibility = View.VISIBLE
    }

    /**
     * Method that hides the progress bar.
     */
    private fun hideProgressBar(){
        progress_bar_home.visibility = View.GONE
    }

    /**
     * @see Fragment.onViewCreated
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgressBar()

        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        adapter = RecyclerViewAdapter(list)
        recyclerView.adapter = adapter

        hideProgressBar()
    }

}