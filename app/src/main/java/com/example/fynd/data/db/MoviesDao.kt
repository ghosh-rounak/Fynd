package com.example.fynd.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fynd.data.db.entities.Movie


@Dao
interface MoviesDao {

    @Insert
    suspend fun insertMovie(movie: Movie): Long  //returns id of new row

    @Delete
    suspend fun deleteMovie(movie: Movie) : Int  //returns no of rows deleted

    @Query("SELECT * FROM  movies_table")
    fun getAllSavedMovies(): LiveData<List<Movie>>
}