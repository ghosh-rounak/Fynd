package com.example.fynd.ui.movies

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fynd.R
import com.example.fynd.data.network.responses.Result
import com.example.fynd.databinding.ActivityMoviesBinding
import com.example.fynd.ui.cart.CartActivity
import com.example.fynd.ui.adapters.MoviesAdapter
import com.example.fynd.ui.movie_detail.MovieDetailActivity
import com.example.fynd.ui.search.SearchActivity
import com.google.android.material.snackbar.Snackbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MoviesActivity : AppCompatActivity() , KodeinAware {
    override val kodein by kodein()
    private val factory: MoviesViewModelFactory by instance()

    private lateinit var binding: ActivityMoviesBinding
    private lateinit var viewModel: MoviesViewModel
    private lateinit var savedMoviesCountText: TextView
    //private lateinit var searchView :SearchView
    private var totalNumberOfMoviesSaved = 0

    private lateinit var moviesAdapter: MoviesAdapter
    private var gridLayoutManager: GridLayoutManager? = null
    private var isScrolling: Boolean = false
    private var currentItems: Int = 0
    private var totalItems: Int = 0
    private var scrollOutItems: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_movies)
        viewModel = ViewModelProvider(this, factory).get(MoviesViewModel::class.java)
        binding.moviesViewModel = viewModel
        binding.lifecycleOwner = this

        setToolbar()

        initRecyclerView()
        createObservers()
        setRefresher()

        Log.d("onCreate", "onCreate: MoviesActivity called")
    }



    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            //called when scrolling starts
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            //called when scrolling done
            super.onScrolled(recyclerView, dx, dy)
            currentItems =
                (binding.recyclerViewUserList.layoutManager!! as GridLayoutManager).childCount
            totalItems =
                (binding.recyclerViewUserList.layoutManager!! as GridLayoutManager).itemCount
            scrollOutItems =
                (binding.recyclerViewUserList.layoutManager!! as GridLayoutManager).findFirstVisibleItemPosition()

            if (isScrolling && ((currentItems + scrollOutItems) == totalItems) && !viewModel.loading && dy > 0) {
                //proceeding towards fetching  next page data
                viewModel.loading = true
                isScrolling = false
                //fetch next page data
                viewModel.getMovies()
            }
        }
    }

    private fun initRecyclerView() {
        if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = GridLayoutManager(this,2,LinearLayoutManager.VERTICAL, false)
        }else{
            //lanscape
            gridLayoutManager = GridLayoutManager(this,4,LinearLayoutManager.VERTICAL, false)
        }

        binding.recyclerViewUserList.layoutManager = gridLayoutManager
        moviesAdapter = MoviesAdapter { selectedItem: Result, position: Int ->
            listItemClicked(
                selectedItem,
                position
            )
        }
        binding.recyclerViewUserList.adapter = moviesAdapter

        //----
        moviesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                Log.d("onCreate", "Scroll Top: ")
                binding.recyclerViewUserList.smoothScrollToPosition(0)
            }
        })

        //----

        binding.recyclerViewUserList.addOnScrollListener(this.scrollListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d("onCreate", "onCreateOptionsMenu: called")
        menuInflater.inflate(R.menu.menu,menu)

        //Added for searchview begins
        val searchItem = menu!!.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("onCreate", "onQueryTextSubmit: $query")
                val i =Intent(this@MoviesActivity, SearchActivity::class.java)
                i.putExtra("query",query)
                startActivity(i)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        //Added for searchview ends


        //Added for Cart Begins
        val cartItem = menu.findItem(R.id.cart)
        val actionView = cartItem.actionView
        savedMoviesCountText = actionView.findViewById(R.id.notification_badge)
        //actionView.setOnClickListener { onOptionsItemSelected(cartItem) }
        createObserverForCartIcon()
        actionView.setOnClickListener { onOptionsItemSelected(cartItem) }
        //Added for Cart Ends

        //load first page method call
        viewModel.getMovies()

        return true
    }

   /* private fun getSavedMoviesCount() {
        totalNumberOfMoviesSaved = 100

    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id= item.itemId
        if(id==R.id.cart){
            startCartScreen()
        }
        return true
    }

    private fun startCartScreen() {
        val i =Intent(this@MoviesActivity, CartActivity::class.java)
        startActivity(i)
    }


    private fun setUpBadge() {
        if(totalNumberOfMoviesSaved==0){
            if(savedMoviesCountText.visibility != View.GONE){
                savedMoviesCountText.visibility = View.GONE
            }
        }else{
            if(totalNumberOfMoviesSaved>99){
                savedMoviesCountText.text = "99+"
            }else{
                savedMoviesCountText.text = totalNumberOfMoviesSaved.toString()
            }
            if(savedMoviesCountText.visibility != View.VISIBLE){
                savedMoviesCountText.visibility = View.VISIBLE
            }
        }
    }

    private fun createObservers() {
        viewModel.mProgData.observe(this,{
            if(it){
                Log.d("onCreate", "Observers: mProgData")
                showProgressBar()
            }else{
                Log.d("onCreate", "Observers: mProgData")
                binding.progressBarMovieListLoading.visibility = View.GONE
            }
        })

        viewModel.bProgData.observe(this,{
            if(it){
                Log.d("onCreate", "Observers: bProgData")
                showBottomPrgBar()
            }else{
                Log.d("onCreate", "Observers: bProgData")
                binding.progressBarMovieList.visibility = View.GONE
            }
        })

        viewModel.moviesErrorData.observe(this,{
            Log.d("onCreate", "Observers: usersErrorData")
            hideProgressBar()
            if(!viewModel.firstPageLoaded && viewModel.moviesPage==1 && it=="No Internet"){
                showNoInternet()
            }else if(!viewModel.firstPageLoaded && viewModel.moviesPage==1 && it=="Network Failure"){
                binding.refreshButton.visibility = View.VISIBLE
                Toast.makeText(this,"Please Retry Again !!", Toast.LENGTH_LONG).show()
            }
            else if(it=="No Internet"){
                noInternetMsg()
            }
            else{
                if(it!=""){
                    Toast.makeText(this,it, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.moviesListData.observe(this, {
            if (it != null) {
                Log.d("onCreate", "Observers: usersListData")
                moviesAdapter.differ.submitList(it)
                if (!viewModel.firstPageLoaded) {
                    if (viewModel.moviesPage == 2) {
                        viewModel.firstPageLoaded = true
                    }
                }
                //hideProgressBar()
            }
        })


    }

    private fun createObserverForCartIcon() {
        viewModel.savedMovies.observe(this, {
            Log.i("onCreate", it.toString())
            if(it!=null){
                totalNumberOfMoviesSaved = it.size
                setUpBadge()
            }

        })
    }

    private fun setRefresher() {
        binding.swipeRefreshMovieList.setOnRefreshListener {
            if (viewModel.firstPageLoaded) {
                viewModel.getRefreshedMovies()
            } else {
                binding.swipeRefreshMovieList.isRefreshing = false
            }
        }
    }


    private fun showBottomPrgBar() {
        binding.progressBarMovieList.visibility = View.VISIBLE
    }


    private fun showProgressBar() {
        binding.noInternetImage.visibility = View.GONE
        binding.refreshButton.visibility = View.GONE
        binding.progressBarMovieListLoading.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        /*if (binding.progressBarUserList.visibility == View.VISIBLE) {
            binding.progressBarUserList.visibility = View.GONE
        }
        if (binding.progressBarUserListLoading.visibility == View.VISIBLE) {
            binding.progressBarUserListLoading.visibility = View.GONE
        }*/
        if (binding.swipeRefreshMovieList.isRefreshing) {
            binding.swipeRefreshMovieList.isRefreshing = false
        }
        if (binding.noInternetImage.visibility == View.VISIBLE) {
            binding.noInternetImage.visibility = View.GONE
            binding.refreshButton.visibility = View.GONE
        }
    }

    private fun showNoInternet() {
        binding.noInternetImage.visibility = View.VISIBLE
        binding.refreshButton.visibility = View.VISIBLE
        noInternetMsg()

    }

    private fun noInternetMsg() {
        Snackbar.make(
            (this@MoviesActivity.findViewById(android.R.id.content))!!,
            "No Internet",
            Snackbar.LENGTH_SHORT
        ).show()
    }


    //ITEM Click on each item of the Recycler View
    private fun listItemClicked(result: Result, position: Int) {
        Log.d("onCreate", "listItem position clicked : $position")
        Log.d("onCreate", "listItemClicked: $result")
        //To pass:
        val i = Intent(this@MoviesActivity, MovieDetailActivity::class.java)
        i.putExtra("movieDetailObject",result)
        startActivity(i)
    }
}