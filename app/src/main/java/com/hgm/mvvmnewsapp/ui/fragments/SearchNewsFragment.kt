package com.hgm.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hgm.mvvmnewsapp.ui.MainActivity
import com.hgm.mvvmnewsapp.R
import com.hgm.mvvmnewsapp.adapter.NewsAdapter
import com.hgm.mvvmnewsapp.databinding.FragmentSearchNewsBinding
import com.hgm.mvvmnewsapp.ui.NewsViewModel
import com.hgm.mvvmnewsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.hgm.mvvmnewsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.hgm.mvvmnewsapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 搜索新闻片段
 */
class SearchNewsFragment : Fragment() {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private val TAG = "BreakingNewsFragment"
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val binding: FragmentSearchNewsBinding by lazy {
        FragmentSearchNewsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 把 activity 的 viewModel 赋值给 fragment 使用
        viewModel = (activity as MainActivity).viewModel
        // 配置列表
        setupRecyclerView()

        // 点击 item 跳转到文章详情页
        newsAdapter.setOnItemClickListener {
            // 通过导航传参序列化传递对象
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        // 由于搜索每改变一次会请求一次，导致请求变多
        // 所以添加延迟，等用户输入完再请求
        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.getSearchNews(editable.toString())
                    }
                }
            }
        }

        // 监听
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if (isLastPage) {
                            binding.rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // 计算是否滚动到底部
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition =
                layoutManager.findFirstVisibleItemPosition()// 第一个可见的 item 的位置
            val visibleItemCount = layoutManager.childCount// 可见的 item 数量
            val totalItemCount = layoutManager.itemCount// item 的总量

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem =
                firstVisibleItemPosition + visibleItemCount >= totalItemCount// 是否最后一个 item
            val isNotAtBeginning = firstVisibleItemPosition >= 0// 是否第一个 item
            val isTotalMoreVisible = totalItemCount >= QUERY_PAGE_SIZE// 是否还有更多可以加载
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isNotAtBeginning && isAtLastItem && isTotalMoreVisible && isScrolling// 必须满足5个条件才触发分页请求

            if (shouldPaginate) {
                viewModel.getSearchNews(binding.etSearch.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

}