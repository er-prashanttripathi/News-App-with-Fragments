package com.example.newsappbottomnav.ui.layouts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.newslyrecyclerviewretrofit.Article
import com.example.android.newslyrecyclerviewretrofit.News
import com.example.android.newslyrecyclerviewretrofit.NewsService
import com.example.newsappbottomnav.AdapterNews.NewsAdapter
import com.example.newsappbottomnav.R
import com.example.newsappbottomnav.databinding.FragmentLinearSwapBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LinearSwapFragment : Fragment() {
    private lateinit var binding: FragmentLinearSwapBinding//Data Binding -1
    private var articles = mutableListOf<Article>()
    var pageNum = 1
    var totalResults = -1
    lateinit var adapter: NewsAdapter//to access adapter for recyView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        adapter = NewsAdapter(requireContext(), articles)
        val recyclerView = binding.rcvLinearScroll
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition =
                    linearLayoutManager.findFirstVisibleItemPosition()//give position of 1st visible item
                val TotalCount = linearLayoutManager.itemCount//give total count of all news
                val firstVisibleItemView =
                    linearLayoutManager.findViewByPosition(firstVisibleItemPosition)
                val firstVisibleItemLocation = IntArray(2)
                firstVisibleItemView?.getLocationOnScreen(firstVisibleItemLocation)

                //-------------------------------------------------------------------------------------------------------------------------------------------------
                // Use firstVisibleItemLocation as needed
                if (totalResults > linearLayoutManager.itemCount &&
                    linearLayoutManager.findFirstVisibleItemPosition() >= linearLayoutManager.itemCount - 5) {
                    pageNum++
                    getNews()
                }
            }
        })
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_linear_swap, container, false)
    }

    private fun getNews() {
        Log.d("request Page No.", "getNews: request Page No. $pageNum")
        val news = NewsService.newsInstance.headlines("in", pageNum)
        news.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news = response.body()
                if (news != null) {
                    totalResults = news.totalResults
                    Log.d("Api_Calling", news.toString())
                    articles.addAll(news.articles)
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("Api_Calling", "onFailure: ERROR IN FETCHING NEWS", t)
            }
        })
    }


}