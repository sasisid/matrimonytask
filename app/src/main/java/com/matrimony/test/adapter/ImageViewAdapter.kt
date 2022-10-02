package com.matrimony.test.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.matrimony.test.R
import com.matrimony.test.databinding.ImageCardBinding
import com.matrimony.test.interfaces.OnClickListener
import com.matrimony.test.model.ImageDetails
import com.matrimony.test.viewmodel.MainActivityViewModel

class ImageViewAdapter(val arrayList: ArrayList<ImageDetails>,
                       val context: Context,
                       var onClickListener: OnClickListener,): RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ImageViewAdapter.ImageViewHolder {
        var root = LayoutInflater.from(parent.context).inflate(R.layout.image_card,parent,false)
        return ImageViewHolder(root)
    }
    override fun onBindViewHolder(holder: ImageViewAdapter.ImageViewHolder, position: Int) {
        // Set the image using Glide library

        // Set the image using Glide library
        Glide.with(context)
            .load(arrayList[position].url)
            .into(holder.binding.image)

        holder.binding.remove.setOnClickListener(){
            onClickListener.onClickItem(position)

        }

    }

    override fun getItemCount(): Int {
        if(arrayList.size==0){
            Toast.makeText(context,"List is empty",Toast.LENGTH_LONG).show()
        }else{

        }
        return arrayList.size
    }
    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ImageCardBinding = ImageCardBinding.bind(view)
    }

}