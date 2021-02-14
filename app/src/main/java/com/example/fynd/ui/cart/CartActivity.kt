package com.example.fynd.ui.cart

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fynd.R
import com.example.fynd.data.db.entities.Movie
import com.example.fynd.databinding.ActivityCartBinding
import com.example.fynd.ui.adapters.DBMoviesAdapter
import com.example.fynd.ui.movie_detail.MovieDetailActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class CartActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: CartViewModelFactory by instance()
    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: CartViewModel

    private lateinit var dBMoviesAdapter: DBMoviesAdapter
    private var gridLayoutManager: GridLayoutManager? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        viewModel = ViewModelProvider(this, factory).get(CartViewModel::class.java)
        binding.cartViewModel = viewModel
        binding.lifecycleOwner = this

        setToolbar()
        initRecyclerView()
        createObservers()
    }

    private fun setToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = GridLayoutManager(this,2, LinearLayoutManager.VERTICAL, false)
        }else{
            //lanscape
            gridLayoutManager = GridLayoutManager(this,4, LinearLayoutManager.VERTICAL, false)
        }

        binding.recyclerViewSavedMovieList.layoutManager = gridLayoutManager

        dBMoviesAdapter = DBMoviesAdapter( { selectedItem: Movie, position: Int ->
            listItemClicked(
                selectedItem,
                position
            )
        },{ selectedItem: Movie, position: Int ->
            listItemLongClicked(
                selectedItem,
                position
            )
        })
        binding.recyclerViewSavedMovieList.adapter = dBMoviesAdapter

        dBMoviesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                Log.d("onCreate", "Scroll Top: ")
                binding.recyclerViewSavedMovieList.smoothScrollToPosition(0)
            }
        })
    }




    private fun createObservers() {
        viewModel.messageData.observe(this, { it ->
            it.getContentIfNotHandled()?.let {
                when (it) {
                    "DismissLoading"->{
                        binding.progressBarSavedMovieListLoading.visibility=View.GONE
                    }
                    else -> {
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        viewModel.savedMovies.observe(this, {
            Log.i("onCreate", it.toString())
            if (it != null) {
                Log.d("onCreate", "saved movie list size: ${it.size}")
                dBMoviesAdapter.differ.submitList(it)
            }

        })
    }


    //ITEM Click on each item of the Recycler View
    private fun listItemClicked(movie: Movie, position: Int) {
        Log.d("onCreate", "listItem position clicked : $position")
        Log.d("onCreate", "listItemClicked: $movie")
        //To pass:
        val i = Intent(this@CartActivity, MovieDetailActivity::class.java)
        i.putExtra("movieDetailObjectDB",movie)
        startActivity(i)
    }

    //ITEM Long Click on each item of the Recycler View
    private fun listItemLongClicked(movie: Movie, position: Int) : Boolean{
        //do something
        showAlertDelete(movie,position)
        return true
    }

    private fun showAlertDelete(movie: Movie, position: Int) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@CartActivity)
        alertDialog.setTitle("Delete")
        alertDialog.setMessage("Do wanna delete this movie")
        alertDialog.setPositiveButton(
            "yes"
        ) { _, _ ->
            binding.progressBarSavedMovieListLoading.visibility= View.VISIBLE
            viewModel.deleteMovieFromDB(movie,position)
        }
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }
}