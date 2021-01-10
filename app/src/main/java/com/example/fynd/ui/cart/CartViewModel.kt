package com.example.fynd.ui.cart

import android.util.Log
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fynd.data.db.entities.Movie
import com.example.fynd.data.repositories.CartRepository
import com.example.fynd.utils.Event
import kotlinx.coroutines.launch


class CartViewModel(private val repository: CartRepository) : ViewModel(), Observable {

    private val message = MutableLiveData<Event<String>>()
    val messageData: LiveData<Event<String>> //to be Observed from MainActivity for displaying  messages
        get() = message

    val savedMovies: LiveData<List<Movie>> =
        repository.savedMovies // Live Data to be observed from Main Activity



    fun deleteMovieFromDB(movie: Movie,position:Int){
        Log.d("onCreate", "deleteMovieFromDB position passed : $position ")
        deleteMovie(movie)
    }




    //DELETE OPERATION
    private fun deleteMovie(movie: Movie) = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(movie)

        if (noOfRowsDeleted > 0) {
            message.value = Event("DismissLoading")
            message.value = Event("$noOfRowsDeleted Item Deleted Successfully")
        } else {
            message.value = Event("DismissLoading")
            message.value = Event("Error Occurred During Deletion")
        }

    }












    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}