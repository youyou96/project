package com.bird.yy.project.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bird.yy.project.entity.Country
import com.google.android.material.card.MaterialCardView
import com.unlimited.stable.earth.R

class LocationAdapter :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {
    private var itemList: MutableList<Country>? = null
    private var onItemClickListener: OnItemClickListener? = null
    fun setList(itemList: MutableList<Country>) {
        this.itemList = itemList
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, country: Country)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    inner class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var item: Country
        private val cardView = view.findViewById<MaterialCardView>(R.id.item_cardview)
        private val img = view.findViewById<ImageView>(R.id.item_img)
        private val tv = view.findViewById<TextView>(R.id.item_tv)
        private val imgChoose = view.findViewById<ImageView>(R.id.item_choose)


        @SuppressLint("ResourceAsColor")
        fun bind(item: Country) {
            this.item = item
//            if (item.name.equals("Fast Smart Server")) {
//                img.visibility = View.INVISIBLE
//            } else {
            item.src?.let { img.setBackgroundResource(it) }
//            }
            if (item.name?.contains("Fast Smart") == true){
                tv.text = item.name
            }else {
                tv.text = item.name + "  " +item.city
            }

            if (item.isChoose == true) {
                imgChoose.visibility = View.VISIBLE
            } else {
                imgChoose.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return if (itemList == null) {
            0
        } else {
            itemList!!.size + 2
        }
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        if (position == itemCount - 1 || position == itemCount - 2) {
            holder.itemView.visibility = View.INVISIBLE
        } else {
            holder.bind(itemList?.get(position) ?: Country())
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(it, itemList?.get(position) ?: Country())
            }
        }

    }
}

