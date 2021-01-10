package com.example.fynd.data.repositories

import com.example.fynd.data.network.FyndApi
import com.example.fynd.data.network.responses.SearchedMoviesResponse
import retrofit2.Response


class SearchRepository(private val api: FyndApi, private val apiKey:String) {
    suspend fun getSearchedMovies(query:String,page:Int): Response<SearchedMoviesResponse> {
        return api.getSearchedMovies(apiKey,query,page)
    }
}