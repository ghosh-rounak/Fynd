package com.example.fynd.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "movies_table")
data class Movie(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id:Long,
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,
    @ColumnInfo(name = "original_language")
    val originalLanguage: String?,
    @ColumnInfo(name = "original_title")
    val originalTitle: String?,
    @ColumnInfo(name = "overview")
    val overview: String?,
    @ColumnInfo(name = "popularity")
    val popularity: Double?,
    @ColumnInfo(name = "poster_path")
    val posterPath: String?,
    @ColumnInfo(name = "release_date")
    val releaseDate: String?,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double?,
    @ColumnInfo(name = "vote_count")
    val voteCount: Long?
):Serializable


/*
* @Entity(tableName = "movies_table")
data class Movie(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,
    @ColumnInfo(name = "original_language")
    val originalLanguage: String?,
    @ColumnInfo(name = "original_title")
    val originalTitle: String?,
    @ColumnInfo(name = "overview")
    val overview: String?,
    @ColumnInfo(name = "popularity")
    val popularity: Double?,
    @ColumnInfo(name = "poster_path")
    val posterPath: String?,
    @ColumnInfo(name = "release_date")
    val releaseDate: String?,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double?,
    @ColumnInfo(name = "vote_count")
    val voteCount: Int?
):Serializable
* */