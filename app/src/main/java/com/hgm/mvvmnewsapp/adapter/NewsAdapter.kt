package com.hgm.mvvmnewsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hgm.mvvmnewsapp.R
import com.hgm.mvvmnewsapp.models.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivArticleImage = itemView.findViewById<ImageView>(R.id.ivArticleImage)
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val tvSource = itemView.findViewById<TextView>(R.id.tvSource)
        val tvPublishedAt = itemView.findViewById<TextView>(R.id.tvPublishedAt)
        val tvDescription = itemView.findViewById<TextView>(R.id.tvDescription)
    }

    // 通过对比实现局部刷新
    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_article_preview, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        // 给控件依次赋值
        with(article) {
            Glide.with(holder.itemView).load(urlToImage).into(holder.ivArticleImage)
            holder.tvTitle.text = title
            holder.tvSource.text = source?.name
            holder.tvPublishedAt.text = publishedAt
            holder.tvDescription.text = description
            // 设置 item 的点击事件
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(this) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // 添加 item 点击事件
    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}