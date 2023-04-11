package com.hgm.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hgm.mvvmnewsapp.ui.MainActivity
import com.hgm.mvvmnewsapp.R
import com.hgm.mvvmnewsapp.adapter.NewsAdapter
import com.hgm.mvvmnewsapp.databinding.FragmentSavedNewsBinding
import com.hgm.mvvmnewsapp.ui.NewsViewModel

/**
 * 收藏新闻片段
 */
class SavedNewsFragment : Fragment() {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private val binding: FragmentSavedNewsBinding by lazy {
        FragmentSavedNewsBinding.inflate(layoutInflater)
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

        setupRecyclerView()

        // 点击 item 进入详情页
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                resId = R.id.action_savedNewsFragment_to_articleFragment,
                args = bundle
            )
        }


        // 添加 item 触摸事件
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                // 删除文章
                viewModel.deleteArticle(article)
                Snackbar.make(view, "文章删除成功～", Snackbar.LENGTH_SHORT).apply {
                    setAction("撤回") {
                        binding.tvTips.visibility=View.INVISIBLE
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }


        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            if (articles.isEmpty()){
                binding.tvTips.visibility=View.VISIBLE
            }else{
                binding.tvTips.visibility=View.INVISIBLE
                newsAdapter.differ.submitList(articles)
            }
        })
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}