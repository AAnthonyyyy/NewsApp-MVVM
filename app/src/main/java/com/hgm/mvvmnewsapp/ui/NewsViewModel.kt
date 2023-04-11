package com.hgm.mvvmnewsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hgm.mvvmnewsapp.models.Article
import com.hgm.mvvmnewsapp.models.NewsResponse
import com.hgm.mvvmnewsapp.repository.NewsRepository
import com.hgm.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    private var oldSearchQuery: String? = null
    private var newSearchQuery: String? = null

    init {
        getBreakingNews("us")
    }

    /**
     * 获取突发新闻文章
     * @param countryCode String
     * @return Job
     */
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }


    /**
     * 获取搜索新闻文章
     * @param searchQuery String
     * @return Job
     */
    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.getSearchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }


    /**
     * 处理突发新闻文章的 Response
     * @param response Response<NewsResponse>
     * @return Resource<NewsResponse>
     */
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    // 获取新旧的列表数据
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    // 把新数据添加到旧数据中
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    /**
     * 处理搜索新闻文章的 Response
     * @param response Response<NewsResponse>
     * @return Resource<NewsResponse>
     */
    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                } else {
                    searchNewsPage++
                    // 获取新旧的列表数据
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    // 把新数据添加到旧数据中
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    /**
     * 保存文章
     * @param article com.hgm.mvvmnewsapp.models.Article
     * @return Job
     */
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    /**
     * 获取收藏的所有文章
     * @return LiveData<List<com.hgm.mvvmnewsapp.models.Article>>
     */
    fun getSavedNews() = newsRepository.getSavedNews()

    /**
     * 删除文章
     * @param article com.hgm.mvvmnewsapp.models.Article
     * @return Job
     */
    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }


    /**
     * 联网检测
     */
    //private fun hasInternetConnection(): Boolean {
    //    val connectivityManager =
    //        getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    //
    //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    //        val activeNetWork = connectivityManager.activeNetwork ?: return false
    //        val capabilities =
    //            connectivityManager.getNetworkCapabilities(activeNetWork) ?: return false
    //        return when {
    //            capabilities.hasTransport(TRANSPORT_WIFI) -> true
    //            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
    //            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
    //            else -> false
    //        }
    //    } else {
    //        connectivityManager.activeNetworkInfo?.run {
    //            return when (type) {
    //                TYPE_WIFI -> true
    //                TYPE_MOBILE -> true
    //                TYPE_ETHERNET -> true
    //                else -> false
    //            }
    //        }
    //    }
    //    return false
    //}
}