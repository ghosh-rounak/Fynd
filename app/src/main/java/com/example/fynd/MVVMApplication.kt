package com.example.fynd

import androidx.multidex.MultiDexApplication
import com.example.fynd.data.db.AppDatabase
import com.example.fynd.data.network.FyndApi
import com.example.fynd.data.preferences.PreferenceProvider
import com.example.fynd.data.repositories.CartRepository
import com.example.fynd.data.repositories.MovieDetailRepository
import com.example.fynd.data.repositories.MoviesRepository
import com.example.fynd.data.repositories.SearchRepository
import com.example.fynd.ui.cart.CartViewModelFactory
import com.example.fynd.ui.movie_detail.MovieDetailViewModelFactory
import com.example.fynd.ui.movies.MoviesViewModelFactory
import com.example.fynd.ui.search.SearchViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class MVVMApplication : MultiDexApplication(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MVVMApplication))

        bind() from singleton { FyndApi() } //For Retrofit Interface that contains all API endpoints
        bind() from singleton { AppDatabase(instance()) } //For Room Database class
        bind() from singleton { PreferenceProvider(instance()) }//For Shared pref

        //for movies module
        bind() from singleton { MoviesRepository(instance(),instance(),BuildConfig.API_KEY_TMDB) } // For MoviesRepository
        bind() from provider { MoviesViewModelFactory(instance()) } //For MoviesViewModelFactory

        //for search movies module
        bind() from singleton { SearchRepository(instance(),BuildConfig.API_KEY_TMDB) } // For SearchRepository
        bind() from provider { SearchViewModelFactory(instance()) } //For SearchViewModelFactory

        //for movie detail module
        bind() from singleton { MovieDetailRepository(instance()) } // For MovieDetailRepository
        bind() from provider { MovieDetailViewModelFactory(instance()) } //For MovieDetailViewModelFactory

        //for cart module
        bind() from singleton { CartRepository(instance()) } // For CartRepository
        bind() from provider { CartViewModelFactory(instance()) } //For CartViewModelFactory



    }

}