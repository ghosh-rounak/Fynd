package com.example.fynd.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fynd.R
import com.example.fynd.data.db.entities.Movie
import com.example.fynd.databinding.ListMovieDbBinding

class DBMoviesAdapter(private val clickListener:(Movie, Int)->Unit,private val longClickListener:(Movie, Int)->Boolean)
    : RecyclerView.Adapter<DBMovieViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DBMovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding : ListMovieDbBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_movie_db,parent,false)
        return DBMovieViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


  /*  //Added starts
    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position

    //Added ends*/



    override fun onBindViewHolder(holder: DBMovieViewHolder, position: Int) {
        holder.bind(differ.currentList[position],clickListener,longClickListener)
    }

}

class DBMovieViewHolder(private val binding: ListMovieDbBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(movie: Movie, clickListener:(Movie, Int)->Unit,longClickListener:(Movie,Int)->Boolean){
        binding.movieImg.setImageBitmap(null)
        binding.movie = movie
        binding.executePendingBindings()

        binding.layoutListItem.setOnClickListener{
            clickListener(movie,adapterPosition)
        }

        binding.layoutListItem.setOnLongClickListener {
            longClickListener(movie,adapterPosition)
        }
    }


}