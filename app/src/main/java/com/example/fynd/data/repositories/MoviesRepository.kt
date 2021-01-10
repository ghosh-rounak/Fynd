package com.example.fynd.data.repositories

import com.example.fynd.data.db.AppDatabase
import com.example.fynd.data.network.FyndApi
import com.example.fynd.data.network.responses.MoviesResponse
import retrofit2.Response


class MoviesRepository(private val api: FyndApi, db: AppDatabase, private val apiKey:String) {

    val savedMovies = db.getMoviesDao().getAllSavedMovies()

    suspend fun getMovies(page:Int): Response<MoviesResponse> {
        return api.getMovies(apiKey,page)
    }
}