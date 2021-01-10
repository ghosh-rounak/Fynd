package com.example.fynd.ui.movie_detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.fynd.R
import com.example.fynd.data.db.entities.Movie
import com.example.fynd.data.network.responses.Result
import com.example.fynd.databinding.ActivityMovieDetailBinding
import com.google.android.material.snackbar.Snackbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MovieDetailActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: MovieDetailViewModelFactory by instance()
    private lateinit var binding: ActivityMovieDetailBinding
    private lateinit var viewModel: MovieDetailViewModel
    private  var result: Result?=null
    private var movie:Movie?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail)
        viewModel = ViewModelProvider(this, factory).get(MovieDetailViewModel::class.java)
        binding.movieDetailViewModel = viewModel
        binding.lifecycleOwner = this

        setToolbar()
        createObserver()
        getMovieDetailObjectPassed()
    }

    private fun getMovieDetailObjectPassed() {
        if(intent.getSerializableExtra("movieDetailObject")!=null){
            result = intent.getSerializableExtra("movieDetailObject") as Result
        }
        if(result!=null){
            setMovieDetailScreen(result!!)
        }else{
            movie = intent.getSerializableExtra("movieDetailObjectDB") as Movie
            movie?.let { setMovieDetailScreenFromDB(it) }
        }

    }

    private fun setMovieDetailScreenFromDB(movie: Movie) {
        if(movie.posterPath!=null){
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500"+movie.posterPath)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("onCreate","image loading error: ${e.toString()}")
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(binding.movieDetailImg)
        }
        binding.movieName.text = movie.originalTitle
        binding.movieDesc.text = movie.overview
    }

    private fun setMovieDetailScreen(result: Result) {
        if(result.posterPath!=null){
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500"+result.posterPath)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("onCreate","image loading error: ${e.toString()}")
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(binding.movieDetailImg)
        }

        binding.movieName.text = result.originalTitle
        binding.movieDesc.text = result.overview
    }

    private fun setToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(result!=null){
            menuInflater.inflate(R.menu.menu_movie_detail,menu)
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id= item.itemId
        if(id==R.id.save){
            if(result!=null){
                binding.saveMovieProgressBar.visibility = View.VISIBLE
                viewModel.saveInDB(result!!)
            }

        }
        return true
    }

    private fun createObserver() {
        viewModel.messageData.observe(this, { it ->
            it.getContentIfNotHandled()?.let {
                when (it) {
                    "SQLiteConstraintException" -> {
                        showAlreadySavedAlertDialog()
                    }
                    "DismissLoading"->{
                        binding.saveMovieProgressBar.visibility=View.GONE
                    }
                    "Save Success" -> {
                        Snackbar.make(
                            (this@MovieDetailActivity.findViewById(android.R.id.content))!!,
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun showAlreadySavedAlertDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@MovieDetailActivity)
        alertDialog.setTitle("Already Saved!")
        alertDialog.setMessage("This movie is already saved earlier by you")
        alertDialog.setPositiveButton(
            "Ok"
        ) { _, _ ->


        }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }


}