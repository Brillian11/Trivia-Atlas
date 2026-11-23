package com.example.data.remote

import com.example.data.model.WikipediaQueryResponseDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface WikipediaApiService {

    @GET
    suspend fun getRandomArticles(
        @Url endpointUrl: String,
        @Query("action") action: String = "query",
        @Query("generator") generator: String = "random",
        @Query("grnnamespace") namespace: Int = 0,
        @Query("grnlimit") limit: Int = 20,
        @Query("prop") prop: String = "extracts|pageimages|info|description",
        @Query("exintro") exIntro: Int = 1,
        @Query("explaintext") explainText: Int = 1,
        @Query("exchars") exChars: Int = 650,
        @Query("exlimit") exLimit: String = "max",
        @Query("piprop") piProp: String = "thumbnail",
        @Query("pilicense") piLicense: String = "any",
        @Query("pithumbsize") piThumbSize: Int = 800,
        @Query("pilimit") piLimit: String = "max",
        @Query("inprop") inProp: String = "url",
        @Query("redirects") redirects: Int = 1,
        @Query("format") format: String = "json",
        @Query("formatversion") formatVersion: Int = 2,
        @Header("User-Agent") userAgent: String = USER_AGENT,
        @Header("Cache-Control") cacheControl: String = "no-cache, no-store"
    ): WikipediaQueryResponseDto

    @GET
    suspend fun getFullArticle(
        @Url endpointUrl: String,
        @Query("action") action: String = "query",
        @Query("pageids") pageId: Long,
        @Query("prop") prop: String = "extracts|pageimages|info|description",
        @Query("explaintext") explainText: Int = 1,
        @Query("piprop") piProp: String = "thumbnail",
        @Query("pilicense") piLicense: String = "any",
        @Query("pithumbsize") piThumbSize: Int = 1000,
        @Query("inprop") inProp: String = "url",
        @Query("redirects") redirects: Int = 1,
        @Query("format") format: String = "json",
        @Query("formatversion") formatVersion: Int = 2,
        @Header("User-Agent") userAgent: String = USER_AGENT,
        @Header("Cache-Control") cacheControl: String = "no-cache"
    ): WikipediaQueryResponseDto

    @GET
    suspend fun searchArticles(
        @Url endpointUrl: String,
        @Query("action") action: String = "query",
        @Query("generator") generator: String = "search",
        @Query("gsrsearch") searchQuery: String,
        @Query("gsrnamespace") namespace: Int = 0,
        @Query("gsrlimit") limit: Int = 8,
        @Query("prop") prop: String = "extracts|pageimages|info|description",
        @Query("exintro") exIntro: Int = 1,
        @Query("explaintext") explainText: Int = 1,
        @Query("exchars") exChars: Int = 350,
        @Query("exlimit") exLimit: String = "max",
        @Query("piprop") piProp: String = "thumbnail",
        @Query("pilicense") piLicense: String = "any",
        @Query("pithumbsize") piThumbSize: Int = 500,
        @Query("pilimit") piLimit: String = "max",
        @Query("inprop") inProp: String = "url",
        @Query("redirects") redirects: Int = 1,
        @Query("format") format: String = "json",
        @Query("formatversion") formatVersion: Int = 2,
        @Header("User-Agent") userAgent: String = USER_AGENT
    ): WikipediaQueryResponseDto

    @GET
    suspend fun getArticleByTitle(
        @Url endpointUrl: String,
        @Query("action") action: String = "query",
        @Query("titles") titles: String,
        @Query("prop") prop: String = "extracts|pageimages|info|description",
        @Query("explaintext") explainText: Int = 1,
        @Query("piprop") piProp: String = "thumbnail",
        @Query("pilicense") piLicense: String = "any",
        @Query("pithumbsize") piThumbSize: Int = 1000,
        @Query("inprop") inProp: String = "url",
        @Query("redirects") redirects: Int = 1,
        @Query("format") format: String = "json",
        @Query("formatversion") formatVersion: Int = 2,
        @Header("User-Agent") userAgent: String = USER_AGENT
    ): WikipediaQueryResponseDto

    companion object {
        private const val USER_AGENT =
            "TriviaAtlas/1.0 (https://triviaatlas.app; contact@triviaatlas.app)"

        fun apiUrlForSubdomain(wikiSubdomain: String): String =
            "https://$wikiSubdomain.wikipedia.org/w/api.php"

        fun create(): WikipediaApiService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(WikipediaApiService::class.java)
        }
    }
}
