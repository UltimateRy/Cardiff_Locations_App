package com.example.locationapp

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.security.KeyStore

class RecyclerViewAdapter(private val locationList : ArrayList<Location>,
    private val listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.MyViewHolder, position: Int) {
        val location : Location = locationList[position]
        holder.name.text = location.Name

        val locName = location.Name



        val storageRef = FirebaseStorage.getInstance().reference.child("images/$locName.jpg")

        //Log.e("Image : ", storageRef)

        val localfile = File.createTempFile("tempImage", "jpg")


        //Glide.with(holder.itemView)
        //    .load(storageRef.getFile(localfile))
        //    .into(holder.img)


        storageRef.getFile(localfile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            holder.img.setImageBitmap(bitmap)

        }.addOnFailureListener{

            Log.e("Image View : ", "Failed to get image")
        }

    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    public inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener{
        val name : TextView = itemView.findViewById(R.id.tvName)
        val img : ImageView = itemView.findViewById(R.id.imgLandmark)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}