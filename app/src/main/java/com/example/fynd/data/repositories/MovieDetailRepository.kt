package com.example.fynd.data.repositories

import com.example.fynd.data.db.AppDatabase
import com.example.fynd.data.db.entities.Movie

class MovieDetailRepository(
    private val db: AppDatabase
) {

    suspend fun insertMovie(movie: Movie): Long {
        return db.getMoviesDao().insertMovie(movie)
    }



}