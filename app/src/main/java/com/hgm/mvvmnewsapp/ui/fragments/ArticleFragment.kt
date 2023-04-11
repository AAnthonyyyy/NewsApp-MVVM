package com.hgm.mvvmnewsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.hgm.mvvmnewsapp.ui.MainActivity
import com.hgm.mvvmnewsapp.databinding.FragmentArticleBinding
import com.hgm.mvvmnewsapp.ui.NewsViewModel


class ArticleFragment : Fragment() {

    private val binding: FragmentArticleBinding by lazy {
        FragmentArticleBinding.inflate(layoutInflater)
    }
    lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初始化
        viewModel = (activity as MainActivity).viewModel
        // 接收传递过来的文章
        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            //loadUrl(article.url)
            article.url?.let { loadUrl(it) }
        }

        // 收藏文章
        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "文章收藏成功！", Snackbar.LENGTH_SHORT).show()
        }
    }
}