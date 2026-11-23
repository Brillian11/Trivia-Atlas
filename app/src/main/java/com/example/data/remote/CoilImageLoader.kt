package com.example.data.remote

import android.content.Context
import coil.ImageLoader
import okhttp3.OkHttpClient

fun createAtlasImageLoader(context: Context): ImageLoader {
    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header(
                    "User-Agent",
                    "TriviaAtlas/1.0 (https://triviaatlas.app; contact@triviaatlas.app)"
                )
                .build()
            chain.proceed(request)
        }
        .build()

    return ImageLoader.Builder(context.applicationContext)
        .okHttpClient(client)
        .crossfade(true)
        .build()
}
