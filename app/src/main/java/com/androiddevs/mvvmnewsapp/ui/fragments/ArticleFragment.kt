package com.androiddevs.mvvmnewsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.model.Article
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_article.*
import java.lang.Exception

class ArticleFragment : Fragment(R.layout.fragment_article), View.OnClickListener {
    lateinit var viewModel: NewsViewModel
    lateinit var article: Article
    val TAG = "ArticleFragment"
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        article = args.article

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url.toString())
        }

        fab.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                try {
                    viewModel.saveArticle(article = article)
                    Snackbar.make(v,"Article Saved Successfully", Snackbar.LENGTH_SHORT).show()
                } catch (message:Exception){
                    Log.e(TAG, "An error occured: $message")
                }

            }
        }
    }
}