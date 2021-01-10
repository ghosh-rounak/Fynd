package com.example.fynd.ui.movies

import android.util.Log
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fynd.data.db.entities.Movie
import com.example.fynd.data.network.responses.MoviesResponse
import com.example.fynd.data.network.responses.Result
import com.example.fynd.data.repositories.MoviesRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MoviesViewModel(private val repository: MoviesRepository) : ViewModel(), Observable {
    var loading: Boolean = false

    private var moviesError = MutableLiveData<String>()
    val moviesErrorData: LiveData<String>
        get() = moviesError

    private var moviesList = MutableLiveData<MutableList<Result>>()
    val moviesListData: LiveData<MutableList<Result>>
        get() = moviesList

    private var mProg = MutableLiveData<Boolean>()
    val mProgData: LiveData<Boolean>
        get() = mProg

    private var bProg = MutableLiveData<Boolean>()
    val bProgData: LiveData<Boolean>
        get() = bProg

    private var list: MutableList<Result>? = null
    var moviesPage = 1 //starting page no which is incremented after every successful Retrofit Call
    private var lastPage = 1  //last page no which is updated after every successful Retrofit Call
    var firstPageLoaded = false


    val savedMovies: LiveData<List<Movie>> =
        repository.savedMovies // Live Data to be observed from Main Activity


    init {

    }

    fun getMovies() = viewModelScope.launch {
        safeMoviesCall(moviesPage)
    }

    private suspend fun safeMoviesCall(pageNumber: Int) {
        Log.d("onCreate", "Page Called:  $pageNumber")
        if (moviesPage <= lastPage) {
            if (moviesPage > 1) {
                //show bottom progress bar only
                bProg.postValue(true)
            } else {
                //show middle progress bar only
                mProg.postValue(true)
            }
            try {
                val response = repository.getMovies(pageNumber)
                handleMoviesResponse(response)

            } catch (t: Throwable) {
                when (t) {
                    is IOException -> {
                        Log.d("onCreate", "safeMoviesCall: IOException")
                        reset()
                        moviesError.postValue("No Internet") //No Internet
                    }
                    else -> {
                        reset()
                        moviesError.postValue(t.message ?: "Unknown Error")
                        t.printStackTrace()
                    }
                }
            }
        } else {
            //end of all pages
            Log.d("onCreate", "createObservers Cust Msg:")
        }

    }

    private fun handleMoviesResponse(response: Response<MoviesResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                lastPage = resultResponse.totalPages
                moviesPage++
                if (list == null) {
                    list = resultResponse.results as MutableList<Result>
                } else {
                    val newList = resultResponse.results
                    list!!.addAll(newList)
                }
                resetOnSuccess()
                moviesList.postValue(list)
                loading = false
            }
        } else {
            reset()
            moviesError.postValue(response.message())
        }
    }

    fun getRefreshedMovies() = viewModelScope.launch {
        safeRefreshMoviesCall(moviesPage)
    }

    private suspend fun safeRefreshMoviesCall(pageNumber: Int) {
        Log.d("onCreate", "Page Called:  $pageNumber")
        if (moviesPage <= lastPage) {
            mProg.postValue(true)
            try {
                val response = repository.getMovies(pageNumber)
                handleRefreshMoviesResponse(response)
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> {
                        reset()
                        moviesError.postValue("No Internet") //No Internet
                    }
                    else -> {
                        reset()
                        moviesError.postValue(t.message ?: "Unknown Error")
                        t.printStackTrace()
                    }
                }
            }
        } else {
            //end of all pages and so restart again
            list = null
            moviesPage = 1
            lastPage = 1
            safeMoviesCall(1)
        }

    }

    private fun handleRefreshMoviesResponse(response: Response<MoviesResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                lastPage = resultResponse.totalPages
                moviesPage++
                list = resultResponse.results as MutableList<Result>
                resetOnSuccess()
                moviesList.postValue(list)
                loading = false
            }
        } else {
            reset()
            moviesError.postValue(response.message())
        }
    }


    private fun resetOnSuccess() { //used to reset all loading states and error state
        mProg.postValue(false)
        bProg.postValue(false)
        moviesError.postValue("")
    }

    private fun reset() { //used to reset all loading states
        mProg.postValue(false)
        bProg.postValue(false)
    }

    fun onRefreshClick() {
        Log.d("onCreate", "onRefreshClick From MoviesViewModel: ")
        getMovies()
    }















    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}