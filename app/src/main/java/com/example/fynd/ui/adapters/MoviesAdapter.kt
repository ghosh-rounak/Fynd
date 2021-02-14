package com.example.fynd.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fynd.R
import com.example.fynd.data.network.responses.Result
import com.example.fynd.databinding.ListMovieBinding


class MoviesAdapter(private val clickListener:(Result,Int)->Unit)
    : RecyclerView.Adapter<MovieViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding : ListMovieBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_movie,parent,false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

   /* //Added starts
    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position

    //Added ends*/

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(differ.currentList[position],clickListener)
    }

}

class MovieViewHolder(private val binding: ListMovieBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(result: Result,clickListener:(Result,Int)->Unit){
        binding.movieImg.setImageBitmap(null)
        binding.result = result
        binding.executePendingBindings()

        binding.layoutListItem.setOnClickListener{
            clickListener(result,adapterPosition)
        }
    }


}