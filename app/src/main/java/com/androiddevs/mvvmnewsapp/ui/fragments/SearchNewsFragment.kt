package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.util.Constants
import com.androiddevs.mvvmnewsapp.ui.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.androiddevs.mvvmnewsapp.ui.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "SearchNewsFragment"

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()

        newsAdapter.setOnitemClickListeners {

            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    showHideProgressBar(View.INVISIBLE)
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    showHideProgressBar(View.INVISIBLE)
                    response.message?.let { message ->
                        Toast.makeText(activity!!, "An error occured: $message", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showHideProgressBar(View.VISIBLE)
                }
            }

        })
    }

    private fun showHideProgressBar(value: Int) {
        paginationProgressBar.visibility = value
        isLoading = value != View.INVISIBLE
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val shouldPaginate =
                isNotLoadingAndNotLastPage && isLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.searchNews(etSearch.text.toString())
                isScrolling = false
            } else {
                rvSearchNews.setPadding(0, 0, 0, 0)
            }
        }
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }

    }
}