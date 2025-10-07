package com.example.adsbubble.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.adsbubble.R
import com.example.adsbubble.data.db.Ad

class AdsAdapter(private val onClick: (Ad)->Unit) : ListAdapter<Ad, AdsAdapter.VH>(DIFF) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Ad>() {
            override fun areItemsTheSame(old: Ad, new: Ad) = old.token == new.token
            override fun areContentsTheSame(old: Ad, new: Ad) = old == new
        }
    }
    inner class VH(v: View): RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.adTitle)
        val token: TextView = v.findViewById(R.id.adToken)
        val img: ImageView = v.findViewById(R.id.adImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ad, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ad = getItem(position)
        holder.title.text = ad.title
        holder.token.text = ad.token
        holder.img.load(ad.imageUrl) { placeholder(R.drawable.ic_launcher_foreground) }
        holder.itemView.setOnClickListener { onClick(ad) }
    }
}
