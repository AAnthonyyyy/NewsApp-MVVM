package com.hgm.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hgm.mvvmnewsapp.models.Article

@Dao
interface ArticleDao {
    // 插入或更新文章
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article)

    // 获取所有文章
    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    // 删除文章
    @Delete
    suspend fun deleteArticle(article: Article)
}