package com.example.apimaster

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class MyAdapter(private val context: Activity, private val arr: List<Meme>) :
    RecyclerView.Adapter<MyAdapter.MyViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        val view=LayoutInflater.from(context).inflate(R.layout.item,parent,false)
        return MyViewholder(view)
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val currentItem=arr[position]
        val count=currentItem.preview.size-1
        Picasso.get().load(currentItem.preview[count]).into(holder.image)
        holder.name.text= currentItem.ups.toString()
        holder.img.setImageResource(R.drawable.gradient)
        holder.imageView.setImageResource(R.drawable.baseline_thumb_up_off_alt_24)
    }
    class MyViewholder(view: View):RecyclerView.ViewHolder(view) {
        val image : ShapeableImageView
        val imageView : ImageView
        val img:ImageView
        val name: TextView
        init{
            image= view.findViewById(R.id.image)
            name=view.findViewById(R.id.ups)
            imageView=view.findViewById(R.id.upsImage)
            img=view.findViewById(R.id.endLine)
        }
    }
}