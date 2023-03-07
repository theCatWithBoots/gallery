package com.thesis2.myapplication

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ImageAdapter(private var context: Context, private var imageList: ArrayList<Image>):
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>(){


    class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var image: ImageView? = null

        init{
            image=itemView.findViewById(R.id.row_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_custome_recycler_item, parent, false)
        return ImageViewHolder(view)

    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentImage=imageList[position]
        Glide.with(context)
            .load(currentImage.imagePath)
            .apply(RequestOptions().centerCrop())
            .into(holder.image!!)

        holder.image?.setOnClickListener{

            val intent = Intent(context, ImageFullActivity::class.java)

            var id = currentImage.idColumn
            var col = currentImage.widthColumn
            var height =currentImage.heightColumn
            var curi = currentImage.contentUri

            intent.putExtra("path", currentImage.imagePath)
            intent.putExtra("name", currentImage.imageName)
            intent.putExtra("id", currentImage.idColumn)
            intent.putExtra("width", currentImage.widthColumn)
            intent.putExtra("height", currentImage.heightColumn)
            intent.putExtra("contentUri", currentImage.contentUri.toString())
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return imageList.size
    }


}