package com.example.fynd.ui.movie_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fynd.data.repositories.MovieDetailRepository

@Suppress("UNCHECKED_CAST")
class MovieDetailViewModelFactory(private val repository: MovieDetailRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MovieDetailViewModel::class.java)){
            return MovieDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}