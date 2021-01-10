package com.example.fynd.data.repositories

import com.example.fynd.data.db.AppDatabase
import com.example.fynd.data.db.entities.Movie


class CartRepository(
    private val db: AppDatabase
) {

    val savedMovies = db.getMoviesDao().getAllSavedMovies()



    suspend fun delete(movie: Movie): Int {
        return db.getMoviesDao().deleteMovie(movie)
    }


}