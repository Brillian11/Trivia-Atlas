package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class WikipediaArticle(
    val pageId: Long,
    val title: String,
    val description: String?,
    val extract: String,
    val thumbnailUrl: String?,
    val articleUrl: String?,
    val wikiLang: String,
    val dateKey: String
)

data class GeminiAvailableModel(
    val modelId: String,
    val displayName: String,
    val description: String = ""
)

@JsonClass(generateAdapter = true)
data class GeminiModelsListResponseDto(
    @Json(name = "models") val models: List<GeminiModelDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GeminiModelDto(
    @Json(name = "name") val name: String = "",
    @Json(name = "displayName") val displayName: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "supportedGenerationMethods") val supportedGenerationMethods: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class WikipediaQueryResponseDto(
    @Json(name = "query") val query: WikipediaQueryBodyDto? = null
)

@JsonClass(generateAdapter = true)
data class WikipediaQueryBodyDto(
    @Json(name = "pages") val pages: List<WikipediaPageDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class WikipediaPageDto(
    @Json(name = "pageid") val pageId: Long = 0L,
    @Json(name = "title") val title: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "extract") val extract: String? = null,
    @Json(name = "thumbnail") val thumbnail: WikipediaThumbnailDto? = null,
    @Json(name = "fullurl") val fullUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class WikipediaThumbnailDto(
    @Json(name = "source") val source: String? = null,
    @Json(name = "width") val width: Int? = null,
    @Json(name = "height") val height: Int? = null
)

object WikipediaLanguageMapper {
    fun toWikipediaSubdomain(languageCode: String): String = when (languageCode.lowercase()) {
        "es" -> "es"
        "in", "id" -> "id"
        else -> "en"
    }

    fun targetLanguageName(languageCode: String): String = when (languageCode.lowercase()) {
        "es" -> "Spanish (Español)"
        "in", "id" -> "Indonesian (Bahasa Indonesia)"
        else -> "English"
    }
}
