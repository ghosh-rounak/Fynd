package com.example.fynd.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fynd.data.repositories.MoviesRepository
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class MoviesViewModelFactory(private val repository: MoviesRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MoviesViewModel::class.java)){
            return MoviesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}