package com.example.imagesearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imagesearch.R
import kotlinx.android.synthetic.main.layout_loding_error_footer.view.*

class PhotoLoadStateAdapter (private val retry : () -> Unit)
    :LoadStateAdapter<PhotoLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_loding_error_footer, parent, false)
        return LoadStateViewHolder(v)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadStateViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        init {
            itemView.button_retry_footer.setOnClickListener{
                retry.invoke()
            }
        }

        fun bind (loadState: LoadState){
            itemView.progrss_bar_error_footer.isVisible = loadState is LoadState.Loading
            itemView.button_retry_footer.isVisible = loadState !is LoadState.Loading
            itemView.textview_error_footer.isVisible = loadState !is LoadState.Loading

        }
    }
}