package com.example.fynd.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fynd.data.repositories.SearchRepository
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(private val repository: SearchRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchViewModel::class.java)){
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}