package com.example.stadium.recyclerview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.scaffold.R
import com.example.stadium.MainActivity
import com.example.stadium.Question
import com.example.stadium.StadiumApplication
import com.example.stadium.StadiumInfoActuvity

class StadiumRecyclerView (val items: List<Question>, val mContext: Context) : RecyclerView.Adapter<StadiumViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StadiumViewHolder {
        val view = LayoutInflater.from(StadiumApplication.getAppContext()).inflate(R.layout.stadium_recycler_view_item, parent, false)
        return StadiumViewHolder(view)
    }

    override fun onBindViewHolder(holder: StadiumViewHolder, position: Int) {
        val positionItem = items[position]
        holder.stadiumImage.setImageResource(positionItem.resStadiumImage)
        holder.stadiumName.text = positionItem.nameStadium
        holder.stadiumName.setOnClickListener{view->
            //add class Intent
           // val intent = Intent(StadiumApplication.getAppContext(), StadiumInfoActuvity::class.java)
          //  intent.putExtra("POSITION", position)
          //  ContextCompat.startActivity(StadiumApplication.getAppContext(), intent, null )
            //finish need or not need
           val activity =   mContext as MainActivity
            activity.startStadiumInfoActicity(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}