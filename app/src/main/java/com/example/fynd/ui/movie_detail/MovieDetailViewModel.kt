package com.example.fynd.ui.movie_detail

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fynd.data.db.entities.Movie
import com.example.fynd.data.network.responses.Result
import com.example.fynd.data.repositories.MovieDetailRepository
import com.example.fynd.utils.Event
import kotlinx.coroutines.launch

class MovieDetailViewModel(private val repository: MovieDetailRepository) : ViewModel(), Observable {

    private val message = MutableLiveData<Event<String>>()
    val messageData: LiveData<Event<String>> //to be Observed from MainActivity for displaying  messages
        get() = message



    fun saveInDB(result: Result){
        Log.d("onCreate", "saveInDB: called $result")
        val movie = Movie(
            result.id,
            result.backdropPath,
            result.originalLanguage,
            result.originalTitle,
            result.overview,
            result.popularity,
            result.posterPath,
            result.releaseDate,
            result.title,
            result.voteAverage,
            result.voteCount
        )

        insert(movie)
    }

    //INSERT OPERATION
    private fun insert(movie: Movie) = viewModelScope.launch {
        try {
            val newRowId = repository.insertMovie(movie)
            Log.d("onCreate", "newRowId Inserted: $newRowId")
            message.value = Event("DismissLoading")
            message.value = Event("Save Success")
        }catch (e:Exception){
            message.value = Event("DismissLoading")
            if(e is SQLiteConstraintException){
                message.value = Event("SQLiteConstraintException")
            }else{
                e.printStackTrace()
                message.value = Event("Error Occurred during Insertion")
            }

        }
    }


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}