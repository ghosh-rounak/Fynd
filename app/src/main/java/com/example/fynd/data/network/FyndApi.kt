package com.example.fynd.data.network

import com.example.fynd.data.network.responses.MoviesResponse
import com.example.fynd.data.network.responses.SearchedMoviesResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface FyndApi {

    @GET("movie/popular")
    suspend fun getMovies(
        @Query(
            "api_key"
        ) apiKey: String,
        @Query(
            "page"
        ) page: Int
    ): Response<MoviesResponse>


    @GET("search/movie")
    suspend fun getSearchedMovies(
        @Query(
            "api_key"
        ) apiKey: String,
        @Query(
            "query"
        ) query: String,
        @Query(
            "page"
        ) page: Int
    ): Response<SearchedMoviesResponse>





    companion object{
        operator fun invoke(

        ) : FyndApi{

            //Create Logger
            val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val okkHttpclient = OkHttpClient.Builder()
                .callTimeout(15, TimeUnit.SECONDS) //best practice is to reduce timeout and show retry on failure //Added by Rounak
                .addInterceptor(logger)//logcat->verbose->okhttp  (debug process) //Added by Rounak
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FyndApi::class.java)
        }
    }

}