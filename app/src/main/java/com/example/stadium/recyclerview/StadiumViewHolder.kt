package com.example.stadium.recyclerview

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scaffold.R

class StadiumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val stadiumImage: ImageView = view.findViewById<ImageView>(R.id.image_stadium)
    val stadiumName: Button = view.findViewById(R.id.name_stadium)
}