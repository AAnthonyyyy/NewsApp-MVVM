package com.hgm.mvvmnewsapp.repository

import com.hgm.mvvmnewsapp.api.RetrofitManager
import com.hgm.mvvmnewsapp.db.ArticleDatabase
import com.hgm.mvvmnewsapp.models.Article

/**
 * 存储库
 * @constructor
 */
class NewsRepository(
    private val db: ArticleDatabase
) {

    // 获取突发新闻
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitManager.api.getBreakingNews(
            countryCode = countryCode,
            pageNumber = pageNumber
        )

    // 获取搜索新闻
    suspend fun getSearchNews(searchQuery: String, pageNumber: Int) =
        RetrofitManager.api.searchForNews(
            searchQuery = searchQuery,
            pageNumber = pageNumber
        )

    // 插入或更新文章
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    // 获取收藏的所有文章
    fun getSavedNews() = db.getArticleDao().getAllArticles()

    // 删除文章
    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}