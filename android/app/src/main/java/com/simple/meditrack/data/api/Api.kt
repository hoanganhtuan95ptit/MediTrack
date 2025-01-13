package com.simple.meditrack.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/config/translates/{language_code}/translates.json")
    suspend fun syncTranslate(@Path("language_code") languageCode: String): Map<String, String>
}